package net.orbyfied.carbon.util.mc;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WorldUtil {

    public interface BlockShader {

        // if null is returned, dont replace block state
        // if a state is returned, replace block state
        BlockState shade(int x, int y, int z, BlockState state);

    }

    static final Executor executor = Executors.newFixedThreadPool(8);

    public static void updateBlocksAsync(ServerLevel level,
                                         int x1, int y1, int z1,
                                         int x2, int y2, int z2) {
        final int startChunkX = x1 >> 4;
        final int startChunkZ = z1 >> 4;
        final int endChunkX   = x2 >> 4;
        final int endChunkZ   = z2 >> 4;

        for (int cx = startChunkX; cx <= endChunkX; cx++) {
            for (int cz = startChunkZ; cz <= endChunkZ; cz++) {
                final LevelChunk chunk = level.getChunk(cx, cz);
                final int rcx = cx * 16;
                final int rcz = cz * 16;
                executor.execute(() -> {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = -64; y < 320; y++) {
                                BlockState state = chunk.getBlockState(x, y, z);
                                level.sendBlockUpdated(
                                        new BlockPos(rcx + x, y, rcz + z),
                                        state, state, 0
                                );
                            }
                        }
                    }
                });
            }
        }
    }

    public static void modifyBlocksFast(ServerLevel level,
                                        int x1, int y1, int z1,
                                        int x2, int y2, int z2,
                                        BlockShader shader) {
        // calculate chunk locations
        final int startChunkX = x1 >> 4;
        final int startSectY  = y1 >> 4;
        final int startChunkZ = z1 >> 4;
        final int endChunkX   = x2 >> 4;
        final int endSectY    = y2 >> 4;
        final int endChunkZ   = z2 >> 4;

        // calculate start and end locations in chunks
        final int cstartX = x1 % 16;
        final int sstartY = y1 % 16;
        final int cstartZ = z1 % 16;
        final int cendX   = x2 % 16;
        final int sendY   = y2 % 16;
        final int cendZ   = z2 % 16;

        // loop over chunk coords
        for (int cx = startChunkX; cx <= endChunkX; cx++) {
            for (int cz = startChunkZ; cz <= endChunkZ; cz++) {
                // get chunk
                final LevelChunk chunk             = level.getChunk(cx, cz);
                final int fcx = cx;
                final int fcz = cz;
                final int rcx = fcx * 16;
                final int rcz = fcz * 16;
                // schedule task for chunk modification
                executor.execute(() -> {
                    int x  = 0;
                    int z  = 0;
                    int mx = 16;
                    int mz = 16;
                    if (fcx == startChunkX)
                        x = cstartX;
                    if (fcz == startChunkZ)
                        z = cstartZ;
                    if (fcx == endChunkX)
                        mx = cendX + /* for loop is exclusive */ 1;
                    if (fcz == endChunkZ)
                        mz = cendZ + /* for loop is exclusive */ 1;

                    int y  = sstartY;
                    int my = 16;
                    for (int s = startSectY; s <= endSectY; s++) {
                        LevelChunkSection section = chunk.getSection(s);
                        if (s == endSectY)
                            my = sendY + /* for loop exclusive */ 1;
                        for (; y < my; y++)
                            for (int xi = x; xi < mx; xi++)
                                for (int zi = z; zi < mz; zi++)
                                    section.states.getAndSetUnchecked(xi, y, zi,
                                            shader.shade(rcx + xi, s * 16 + y, rcz + z,
                                                    section.states.get(xi, y, zi)));
                        y = 0;
                    }
                });
            }
        }
    }

}
