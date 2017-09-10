
public class ComponentCommunication {

	public int CommunicationBandwidth;
	public int ComponentID1;
	public int ComponentID2;
	public double C2CCommunicationCost;
	public double T2TCommunicationCost;
	public double C2TCommunicationCost;
	public double T2CCommunicationCost;
	public double CommunicationProbability;

	
	//init method
	public ComponentCommunication(int _ComponentID1,int _ComponentID2, int _CommunicationBandwidth)
	{

		ComponentID1=_ComponentID1;
		ComponentID2=_ComponentID2;
		CommunicationBandwidth=_CommunicationBandwidth;
		
	}

	
	//init method
	public ComponentCommunication(int _ComponentID1,int _ComponentID2, double _C2CCommunicationCost, double _T2TCommunicationCost, double _C2TCommunicationCost, double _T2CCommunicationCost, double _CommunicationProbability)
	{

		ComponentID1=_ComponentID1;
		ComponentID2=_ComponentID2;
		C2CCommunicationCost=_C2CCommunicationCost;
		T2TCommunicationCost=_T2TCommunicationCost;
		C2TCommunicationCost=_C2TCommunicationCost;
		T2CCommunicationCost=_T2CCommunicationCost;
		CommunicationProbability=_CommunicationProbability;
	}	
}
