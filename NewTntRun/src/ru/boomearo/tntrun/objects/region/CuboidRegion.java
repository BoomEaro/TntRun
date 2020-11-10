package ru.boomearo.tntrun.objects.region;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class CuboidRegion implements IRegion, ConfigurationSerializable {

	private final Location loc1;
	private final Location loc2;
	
	public CuboidRegion(Location loc1, Location loc2) {
		double xMin = (loc1.getX() < loc2.getX() ? loc1.getX() : loc2.getX());
		double yMin = (loc1.getY() < loc2.getY() ? loc1.getY() : loc2.getY());
		double zMin = (loc1.getZ() < loc2.getZ() ? loc1.getZ() : loc2.getZ());
		
		this.loc1 = new Location(loc1.getWorld(), xMin, yMin, zMin);
		
		double xMax = (loc1.getX() > loc2.getX() ? loc1.getX() : loc2.getX());
		double yMax = (loc1.getY() > loc2.getY() ? loc1.getY() : loc2.getY());
		double zMax = (loc1.getZ() > loc2.getZ() ? loc1.getZ() : loc2.getZ());
		
		this.loc2 = new Location(loc2.getWorld(), xMax, yMax, zMax);
	}
	
	public Location getLocationFirst() {
		return this.loc1;
	}
	
	public Location getLocationSecond() {
		return this.loc2;
	}
	
	@Override
	public boolean isInRegion(Location loc) {
		if (!loc.getWorld().getName().equals(this.loc1.getWorld().getName())) {
			return false;
		}
		
		double xLocIn = loc.getX();
		double yLocIn = loc.getY();
		double zLocIn = loc.getZ();
		
		double Xl = this.loc1.getX();
		double Yl = this.loc1.getY();
		double Zl = this.loc1.getZ();
		
		double Xr = this.loc2.getX();
		double Yr = this.loc2.getY();
		double Zr = this.loc2.getZ();
		
		if ((xLocIn >= Xl) && (xLocIn <= Xr + 1.0D) && (yLocIn >= Yl) && (yLocIn <= Yr + 1.0D) && (zLocIn >= Zl) && (zLocIn <= Zr + 1.0D)) {
			return true;
		}
		
		return false;
	}

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("first", this.loc1);
        result.put("second", this.loc2);
        
        return result;
    }
	
    public static CuboidRegion deserialize(Map<String, Object> args) {
        Location loc1 = null;
        Location loc2 = null;
        
        Object fir = args.get("first");
        if (fir != null) {
            loc1 = (Location) fir;
        }
        
        Object sec = args.get("second");
        if (sec != null) {
            loc2 = (Location) sec;
        }
        
        return new CuboidRegion(loc1, loc2);
    }
}
