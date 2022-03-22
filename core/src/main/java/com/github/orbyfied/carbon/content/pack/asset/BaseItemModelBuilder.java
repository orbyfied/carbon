package com.github.orbyfied.carbon.content.pack.asset;

import com.github.orbyfied.carbon.content.pack.JsonAssetBuilder;
import com.github.orbyfied.carbon.content.pack.PackResource;
import com.github.orbyfied.carbon.content.pack.ResourcePackBuilder;
import com.github.orbyfied.carbon.util.json.JsonDocument;

public class BaseItemModelBuilder extends JsonAssetBuilder {

    public BaseItemModelBuilder(ResourcePackBuilder parent, PackResource loc) {
        super(parent, loc);
    }

    @Override
    public void writeJson(JsonDocument doc) {

    }

    @Override
    public void readJson(JsonDocument doc) {

    }

}
