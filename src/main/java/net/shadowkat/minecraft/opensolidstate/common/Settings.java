package net.shadowkat.minecraft.opensolidstate.common;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;

@Config(modid = "ossm")
public class Settings {
    public static Storage storage = new Storage();
    public static Components items = new Components();

    public static class Storage {
        @Comment({"EEPROM block size"})
        @Name("eeprom_block_size")
        public int eepromBlksize = 512;

        @Comment("EEPROM sizes (in blocks)")
        @Name("eeprom_sizes")
        public int[] eepromSizes = {256, 256, 512};

        @Comment("How long it takes to fully flash an EEPROM (in seconds)")
        @Name("eeprom_flash_time")
        public double eepromFlashTime = 2;

        @Comment({"Flash block size"})
        @Name("flash_block_size")
        public int flashBlksize = 64;

        @Comment("Flash sizes (in blocks)")
        @Name("flash_sizes")
        public int[] flashSizes = {64, 128, 256};

        @Comment("Emulate a `drive` component.")
        @Name("flash_emulate_drive")
        public boolean flashEmulateDrive = false;

        @Comment({"Disc block size"})
        @Name("disc_block_size")
        public int discBlksize = 2048;

        @Comment("Disc sizes (in blocks)")
        @Name("disc_sizes")
        public int[] discSizes = {1024, 2048, 4096};

        @Comment("Disc speeds")
        @Name("disc_speeds")
        public int[] discSpeeds = {1, 4, 16, 24};
    }

    public static class Components {
        @Comment("SoC Complexity")
        @Name("soc_complexity")
        public int[] socComplexity = {1, 2, 5, 9001};
    }

}
