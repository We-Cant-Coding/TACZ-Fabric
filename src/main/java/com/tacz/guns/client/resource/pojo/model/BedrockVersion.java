package com.tacz.guns.client.resource.pojo.model;

public enum BedrockVersion {
    /**
     * Older versions of the bedrock version of the model
     */
    LEGACY("1.10.0"),
    /**
     * The new version of the bedrock model, which will be read by 1.14.0, 1.16.0 1.21.0 onwards.
     */
    NEW("1.12.0");

    private final String version;

    BedrockVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public static boolean isNewVersion(BedrockModelPOJO bedrockModel) {
        String[] checkVersion = bedrockModel.getFormatVersion().split("\\.", 3);
        String[] newVersion = NEW.getVersion().split("\\.", 3);
        if (checkVersion.length == 3 && newVersion.length == 3) {
            return Integer.parseInt(checkVersion[1]) >= Integer.parseInt(newVersion[1]);
        }
        return false;
    }

    public static boolean isLegacyVersion(BedrockModelPOJO bedrockModel) {
        return bedrockModel.getFormatVersion().equals(LEGACY.getVersion());
    }
}
