
public class Game {

	public int ComponentQuantity;
	public Component ComponentSet[]; 
	public int ComponentConsumption[];
	
	public int CommunicationQuantity;
	public ComponentCommunication CommunicationSet[];
	
	
	//init method
	public Game(int _ComponentQuantity, Component[] _ComponentSet, int[] _ComponentConsumption, int _CommunicationQuantity, ComponentCommunication[] _CommunicationSet)
	{
		
		ComponentQuantity=_ComponentQuantity;
		ComponentSet=_ComponentSet;
		ComponentConsumption=_ComponentConsumption;
		CommunicationQuantity=_CommunicationQuantity;
		CommunicationSet=_CommunicationSet;

	}

	//init method
	public Game(int _ComponentQuantity, Component[] _ComponentSet,  int _CommunicationQuantity, ComponentCommunication[] _CommunicationSet)
	{
		
		ComponentQuantity=_ComponentQuantity;
		ComponentSet=_ComponentSet;
		CommunicationQuantity=_CommunicationQuantity;
		CommunicationSet=_CommunicationSet;

	}	
	
	public void showInfo()
	{
		System.out.println("Game Components with Resource Consumption:");
		for(int i=0;i<ComponentQuantity;i++)
			System.out.print(ComponentSet[i].ConsumedResources+" ");
		System.out.println("\nGame Components with Communication Consumption:");
		for(int i=0;i<CommunicationQuantity;i++)
			System.out.print(CommunicationSet[i].ComponentID1+" "+CommunicationSet[i].ComponentID2+" "+CommunicationSet[i].CommunicationBandwidth+"\n");
	}
	
}
