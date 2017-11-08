package cz.pavelfidransky.fav.dbm2.datatype;

/**
 * Regions enumerate.
 * <p>
 * Date: 04.11.2017
 *
 * @author Pavel Fidransky [jsem@pavelfidransky.cz]
 * @see cz.pavelfidransky.fav.dbm2.worker.RegionRetypeWorker
 */
public enum Region {
    /**
     * European Union
     */
    EUROPEAN_UNION("eu"),
    /**
     * United States of America
     */
    UNITED_STATES("us"),
    ;

    private String region;

    Region(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }
}
