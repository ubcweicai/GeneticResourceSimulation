
public class CloudServer {

	int OverallBandwidth;
	int OverallResources;
	int ConsumedBandwidth;
	int ConsumedResources;
	
	public CloudServer(int _OverallBandwidth, int _OverallResources)
	{
		OverallBandwidth=_OverallBandwidth;
		OverallResources=_OverallResources;
		
		ConsumedBandwidth=0;
		ConsumedResources=0;

	}
	
	public void showInfo()
	{
		System.out.println("Cloud Server Status ==> Bandwidth: "+ConsumedBandwidth+"/"+OverallBandwidth+" "+"Resources: "+ConsumedResources+"/"+OverallResources);
	}
	
}
