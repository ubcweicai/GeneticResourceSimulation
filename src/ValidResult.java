
public class ValidResult {

	Boolean valid;
	float[] deviceConsumption;
	float cloudConsumption;
	float[] deviceBandwidth;
	float[] deviceNetworkLatency;
	float cloudBandwidth;
	float[] deviceOverallCost;
	
	float averageDeviceBandwidth;
	float averageDeviceNetworkLatency;
	float averageDeviceConsumption;
	float averageDeviceOverallCost;
	
	
	public ValidResult(Boolean v, float[] dc, float cc, float[] db, float[] dnl, float cb, float[] oc)
	{
		valid=v;
		deviceConsumption=dc;
		cloudConsumption=cc;
		deviceBandwidth=db;
		deviceNetworkLatency=dnl;
		cloudBandwidth=cb;
		deviceOverallCost=oc;
		
		int MobileDeviceQuantity=deviceBandwidth.length;

		float totalDeviceBandwidth=0;
		float totalDeviceNetworkLatency=0;
		float totalDeviceConsumption=0;
		float totalDeviceOverallCost=0;
		
		for(int i=0; i<MobileDeviceQuantity; i++)
		{
			totalDeviceBandwidth+=deviceBandwidth[i];
			totalDeviceNetworkLatency+=deviceNetworkLatency[i];
			totalDeviceConsumption+=deviceConsumption[i];
			totalDeviceOverallCost+=deviceOverallCost[i];
		}
		
		averageDeviceConsumption=totalDeviceConsumption/MobileDeviceQuantity;
		averageDeviceBandwidth=totalDeviceBandwidth/MobileDeviceQuantity;
		averageDeviceNetworkLatency=totalDeviceNetworkLatency/MobileDeviceQuantity;
		averageDeviceOverallCost=totalDeviceOverallCost/MobileDeviceQuantity;

		
	}
	
}
