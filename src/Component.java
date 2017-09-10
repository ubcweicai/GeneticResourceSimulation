
public class Component {

	public int ComponentID;
	public int ConsumedResources;
	public double CloudExecutionCost;
	public double TerminalExecutionCost;
	public double MigrationCost;
	public double ExecutionProbability;

	
	//init method
	public Component(int _ComponentID,int _ConsumedResources)
	{
		ComponentID=_ComponentID;
		ConsumedResources=_ConsumedResources;

	}
	
	//init method
	public Component(int _ComponentID, double _CloudExecutionCost, double _TerminalExecutionCost, double _MigrationCost, double _ExecutionProbability )
	{
		ComponentID=_ComponentID;
		CloudExecutionCost=_CloudExecutionCost;
		TerminalExecutionCost=_TerminalExecutionCost;
		MigrationCost=_MigrationCost;
		ExecutionProbability=_ExecutionProbability;
	}
	
}
