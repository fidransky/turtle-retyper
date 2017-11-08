package cz.pavelfidransky.fav.dbm2.worker;

import java.util.Arrays;

import cz.pavelfidransky.fav.dbm2.datatype.Region;

/**
 * Custom retype worker for parsing region string to enumerate.
 * <p>
 * Date: 04.11.2017
 *
 * @author Pavel Fidransky [jsem@pavelfidransky.cz]
 * @see cz.pavelfidransky.fav.dbm2.datatype.Region
 */
public class RegionRetypeWorker implements IRetypeWorker<Region> {

    @Override
    public Region parse(String string) throws IllegalArgumentException {
        return Arrays.stream(Region.values()).filter(region -> region.getRegion().equals(string)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Region \"" + string + "\" is not supported!"));
    }

}
