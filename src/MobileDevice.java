
public class MobileDevice {

	int DeviceID;
	int RTT;
	
	int AvailableBandwidth;
	int AvailableResources;
	int ConsumedBandwidth;
	int ConsumedResources;
	int TolerateLatency;
	
	public MobileDevice(int _DeviceID, int _RTT, int _AvailableBandwidth, int _AvailableResources, int _TolerateLatency)
	{
		DeviceID=_DeviceID;
		RTT=_RTT;
		AvailableBandwidth=_AvailableBandwidth;
		AvailableResources=_AvailableResources;
		TolerateLatency=_TolerateLatency;
		
		ConsumedBandwidth=0;
		ConsumedResources=0;
	}
	
	public void showInfo()
	{
		System.out.println("Device "+DeviceID+" Status ==> Bandwidth: "+ConsumedBandwidth+"/"+AvailableBandwidth+" "+"Resources: "+ConsumedResources+"/"+AvailableResources);
	}
	
}
