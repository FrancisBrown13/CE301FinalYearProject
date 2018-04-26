public enum RingTones {
    CORNERED("data/cornered.mid"),
    DUEL_OF_FATES("data/duel of fates.mid"),
    MARIO("data/mario.mid"),
    NOKIA_BADINERIE("data/NOKIA_badinerie.mid"),
    NOKIA_BLUE("data/NOKIA_blue.mid"),
    NOKIA_BUFFOON("data/NOKIA_Buffoon.mid"),
    NOKIA_CHRISTMAS("data/NOKIA_christmas.mid"),
    NOKIA_IMPR("data/NOKIA_impr.mid"),
    NOKIA_JUMPER("data/NOKIA_Jumper.mid"),
    NOKIA_LAMB("data/NOKIA_Lamb.mid"),
    NOKIA_MOSQ("data/NOKIA_mosq.mid"),
    NOKIA_RING("data/NOKIA_ring.mid"),
    NOKIA_RINGTONE("data/NOKIA_RINGTONE.mid"),
    NOKIA_Symphony("data/NOKIA_Symphony.mid"),
    OBJECTION("data/objection.mid"),
    SANDSTORM("data/sandstorm.mid"),
    STAR_WARS("data/star wars theme.mid"),
    TICKTICK("data/ticktick.mid"),
    TWINKLE_TWINKLE("data/twinkle_twinkle.mid"),
    ;

    String filename;
    RingTones(String filename){
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
