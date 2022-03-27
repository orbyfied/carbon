package com.github.orbyfied.carbon.command.annotation;

import com.github.orbyfied.carbon.command.CommandEngine;
import com.github.orbyfied.carbon.command.Executable;
import com.github.orbyfied.carbon.command.Node;
import com.github.orbyfied.carbon.command.exception.NodeExecutionException;
import com.github.orbyfied.carbon.registry.Identifier;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

public class BaseAnnotationProcessor {

    protected final CommandEngine engine;

    protected final Object obj;
    protected final Class<?> klass;

    protected Node root;

    public BaseAnnotationProcessor(CommandEngine engine, Object obj) {
        this.engine = engine;
        this.obj    = obj;
        this.klass  = obj.getClass();
    }

    public Object getObject() {
        return obj;
    }

    public CommandEngine getEngine() {
        return engine;
    }

    public Node getBase() {
        return root;
    }

    public BaseAnnotationProcessor compile() {

        // get base descriptor
        BaseCommand baseCommandDesc = klass.getAnnotation(BaseCommand.class);

        // create base node
        root = new Node(baseCommandDesc.name(), null, null)
                .addAliases(baseCommandDesc.aliases());

        // creates the executables
        // and the parameters for them
        for (Method m : klass.getDeclaredMethods()) {
            if (!m.isAnnotationPresent(Subcommand.class)) continue;
            m.setAccessible(true);
            Subcommand desc = m.getAnnotation(Subcommand.class);
            SubcommandParser parser = new SubcommandParser(engine, root, desc.value());
            Node sub = parser.parse();

            // parse parameters
            final ArrayList<String> paramNames = new ArrayList<>(m.getParameterCount());
            Parameter[] parameters = m.getParameters();
            int l = parameters.length;
            for (int i = 2; i < l; i++) {
                Parameter param = parameters[i];
                if (!param.isAnnotationPresent(CommandParameter.class)) continue;
                String name = param.getAnnotation(CommandParameter.class).value();
                if (name.equals(""))
                    name = param.getName();
                paramNames.add(name);
            }

            // get initializer
            try {
                Method initializerSub = klass.getDeclaredMethod(m.getName(), Node.class);
                if (initializerSub.isAnnotationPresent(SubInitializer.class)) {
                    initializerSub.setAccessible(true);
                    initializerSub.invoke(obj, sub);
                }
            } catch (NoSuchMethodException e) {
                // ignore
            } catch (Exception e) {
                e.printStackTrace();
            }

            sub.getComponent(Executable.class).setExecutor((ctx, cmd) -> {
                try {
                    ArrayList<Object> args = new ArrayList<>();
                    args.add(ctx);
                    args.add(cmd);
                    for (String paramn : paramNames) {
                        Identifier pid = new Identifier(null, paramn);
                        args.add(ctx.getArg(pid));
                    }

//                    System.out.println("ctx.args: " + ctx.getArgs() + ", methodargs: " + args);

                    m.invoke(obj, args.toArray());
                } catch (Exception e) {
                    throw new NodeExecutionException(cmd.getRoot(), cmd, e);
                }
            });
        }

        // return
        return this;

    }

    public BaseAnnotationProcessor register() {
        if (root != null)
            engine.register(root);
        return this;
    }

}
