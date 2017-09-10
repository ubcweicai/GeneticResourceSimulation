import java.util.*;


public class Simulation {

	Game g;
	CloudServer cs;
	MobileDevice[] md;
	ArrayList<Boolean> s;
	
	//factor for the latency~consumption
//	float Cloud_Latency_Factor=0.04f;
//	float Device_Latency_Factor=0.035f;
//	float Bandwidth_Latency_Factor=0.4f;
	float Cloud_Latency_Factor;
	float Device_Latency_Factor;
	float Bandwidth_Latency_Factor;
	
	int ComponentQuantity;
	public Component ComponentSet[];
	int MobileDeviceQuantity;
	int CommunicationQuantity;
	public ComponentCommunication CommunicationSet[];
	
	public Simulation(Game _Game, CloudServer _CloudServer, MobileDevice[] _MobileDevices, ArrayList<Boolean> _solution, float[] factors)
	{
		g=_Game;
		cs=_CloudServer;
		md=_MobileDevices;
		s=_solution;
		
		Cloud_Latency_Factor=factors[0];
		Device_Latency_Factor=factors[1];
		Bandwidth_Latency_Factor=factors[2];
		
		ComponentQuantity=g.ComponentQuantity;
		ComponentSet=g.ComponentSet;
		CommunicationQuantity=g.CommunicationQuantity;
		CommunicationSet=g.CommunicationSet;
		MobileDeviceQuantity=md.length;	
	}
	
	public ValidResult validate()
	{
		
		float[] deviceConsumption = this.deviceConsumption();
		float[] cloudConsumption_div = this.cloudConsumption_div();
		float cloudConsumption = this.cloudConsumption_sum();
		DeviceNetwork deviceNetwork = this.deviceNetwork(cloudConsumption_div, deviceConsumption);
		float[] deviceBandwidth = deviceNetwork.deviceBandwidth;
		float[] deviceNetworkLatency = deviceNetwork.deviceNetworkLatency;
		float[] migrationCost=this.migrationCost();
		float[] overallCost = this.overallCost(deviceConsumption, cloudConsumption_div, deviceBandwidth, migrationCost);
		float cloudBandwidth = this.cloudBandwidth(deviceBandwidth);
		
		ValidResult vr=new ValidResult(true, deviceConsumption,cloudConsumption, deviceBandwidth, deviceNetworkLatency, cloudBandwidth, overallCost);
		
		for(int i=0; i<MobileDeviceQuantity; i++)
		{
			if((cloudConsumption>cs.OverallResources)||(cloudBandwidth>cs.OverallBandwidth))
			{
				//System.out.println("fail: cloud");
				vr.valid=false;
			}
			else if((deviceConsumption[i]>md[i].AvailableResources)||(deviceBandwidth[i])>md[i].AvailableBandwidth||(deviceNetworkLatency[i])>md[i].TolerateLatency)
			{
				//System.out.print("fail: device - ");
				if(deviceConsumption[i]>md[i].AvailableResources)
				{
					//System.out.println("resource");
				}else if((deviceBandwidth[i])>md[i].AvailableBandwidth)
				{
					//System.out.println("network");
				}else if((deviceNetworkLatency[i])>md[i].TolerateLatency)
				{
					//System.out.println("latency");
				}
				
				vr.valid=false;
			}
			
		}
		
		//vr.valid=true;
		return vr;
	}
	
	/*
	public float cloudConsumption()
	{
		float cloudConsumption=0;
		
		for(int mdq=0;mdq<MobileDeviceQuantity;mdq++)
		{
			for(int cq=0;cq<ComponentQuantity;cq++)
			{
				//System.out.println(cq+mdq*ComponentQuantity+" "+s.get(cq+mdq*ComponentQuantity)+" "+g.ComponentConsumption[cq]);
				
				if(s.get(cq+mdq*ComponentQuantity)==true)
				{
					cloudConsumption+=g.ComponentConsumption[cq];
				}
			}
		}
		
		//System.out.println("Cloud Consumption: "+cloudConsumption);
		
		return cloudConsumption;
	}
	*/
	
	public float cloudConsumption_sum()
	{
		float[] cloudConsumption_div=new float[MobileDeviceQuantity];
		cloudConsumption_div=this.cloudConsumption_div();
		
		float cloudConsumption=0;
		
		for(int mdq=0;mdq<MobileDeviceQuantity;mdq++)
		{
			cloudConsumption+=cloudConsumption_div[mdq];
		}
		
		//System.out.println("Cloud Consumption: "+cloudConsumption);
		
		return cloudConsumption;
	}
	
	public float[] cloudConsumption_div()
	{
		float[] cloudConsumption=new float[MobileDeviceQuantity];
		
		for(int mdq=0;mdq<MobileDeviceQuantity;mdq++)
		{
			for(int cq=0;cq<ComponentQuantity;cq++)
			{
				//System.out.println(cq+mdq*ComponentQuantity+" "+s.get(cq+mdq*ComponentQuantity)+" "+g.ComponentConsumption[cq]);
				
				if(s.get(cq+mdq*ComponentQuantity)==true)
				{
					//cloudConsumption[mdq]+=g.ComponentConsumption[cq];
					Component c=g.ComponentSet[cq];
					cloudConsumption[mdq]+=c.CloudExecutionCost*c.ExecutionProbability;
				}
			}
			
			//
			for(int ccq=0; ccq<CommunicationQuantity; ccq++)
			{
				ComponentCommunication cc = CommunicationSet[ccq];
				int cq1=cc.ComponentID1;
				int cq2=cc.ComponentID2;
				
				if((s.get(cq1+mdq*ComponentQuantity)==true)&&(s.get(cq2+mdq*ComponentQuantity)==true))
				{
					cloudConsumption[mdq]+=cc.C2CCommunicationCost*cc.CommunicationProbability;
				}
			}
			//
		}
		
		//System.out.println("Cloud Consumption: "+cloudConsumption);
		
		return cloudConsumption;
	}
	
	public float cloudBandwidth(float[] cloudConsumption_div, float[] deviceConsumption)
	{
		
		float cloudBandwidth=0;
		DeviceNetwork deviceNetwork=this.deviceNetwork(cloudConsumption_div, deviceConsumption);
		
		for(int i=0; i<MobileDeviceQuantity; i++)
		{
			cloudBandwidth+=deviceNetwork.deviceBandwidth[i];
		}
		
		//System.out.println("Cloud Bandwidth: "+cloudBandwidth);
		
		return cloudBandwidth;
	}
	
	public float cloudBandwidth(float[] deviceBandwidth)
	{
		
		float cloudBandwidth=0;
		
		for(int i=0; i<MobileDeviceQuantity; i++)
		{
			cloudBandwidth+=deviceBandwidth[i];
		}
		
		//System.out.println("Cloud Bandwidth: "+cloudBandwidth);
		
		return cloudBandwidth;
	}
	
	
	public float[] deviceConsumption()
	{
		float deviceConsumption[]=new float[MobileDeviceQuantity];
		
		for(int mdq=0;mdq<MobileDeviceQuantity;mdq++)
		{
			deviceConsumption[mdq]=0;
			
			for(int cq=0;cq<ComponentQuantity;cq++)
			{
				//System.out.println(cq+mdq*ComponentQuantity+" "+s.get(cq+mdq*ComponentQuantity)+" "+g.ComponentConsumption[cq]);
				
				if(s.get(cq+mdq*ComponentQuantity)==false)
				{
					//deviceConsumption[mdq]+=g.ComponentConsumption[cq];
					Component c=g.ComponentSet[cq];
					deviceConsumption[mdq]+=c.TerminalExecutionCost*c.ExecutionProbability;
				}
			}
			
			for(int ccq=0; ccq<CommunicationQuantity; ccq++)
			{
				ComponentCommunication cc= CommunicationSet[ccq];
				int cq1=cc.ComponentID1;
				int cq2=cc.ComponentID2;
				
				if((s.get(cq1+mdq*ComponentQuantity)==false)&&(s.get(cq2+mdq*ComponentQuantity)==false))
				{
					deviceConsumption[mdq]+=cc.T2TCommunicationCost*cc.CommunicationProbability;
				}
			}
			//System.out.println("Device ["+mdq+"] Consumption: "+deviceConsumption[mdq]);
		}
		
		return deviceConsumption;
		
	}
	
	public float[] migrationCost()
	{
		float migrationCost[]=new float[MobileDeviceQuantity];
		
		for(int mdq=0;mdq<MobileDeviceQuantity;mdq++)
		{
			migrationCost[mdq]=0;
			
			for(int cq=0;cq<ComponentQuantity;cq++)
			{
				//System.out.println(cq+mdq*ComponentQuantity+" "+s.get(cq+mdq*ComponentQuantity)+" "+g.ComponentConsumption[cq]);
				
				if(s.get(cq+mdq*ComponentQuantity)==false)
				{
					//deviceConsumption[mdq]+=g.ComponentConsumption[cq];
					Component c=g.ComponentSet[cq];
					migrationCost[mdq]+=c.MigrationCost;
				}
			}
			
			//System.out.println("Device ["+mdq+"] Consumption: "+deviceConsumption[mdq]);
		}
		
		return migrationCost;
		
	}
	
	public DeviceNetwork deviceNetwork(float[] cloudConsumption_div, float[] deviceConsumption)
	{
		

		
		DeviceNetwork deviceNetwork;
		
		float[] deviceBandwidth=new float[MobileDeviceQuantity];
		float[] deviceNetworkLatency=new float[MobileDeviceQuantity];
		
		for(int mdq=0;mdq<MobileDeviceQuantity;mdq++)
		{
		
			deviceBandwidth[mdq]=0;
			deviceNetworkLatency[mdq]=0;
			
			for(int c=0;c<CommunicationQuantity;c++)
			{
				
				int id1=CommunicationSet[c].ComponentID1+mdq*ComponentQuantity;
				int id2=CommunicationSet[c].ComponentID2+mdq*ComponentQuantity;
				Boolean id1_boolean=s.get(id1);
				Boolean id2_boolean=s.get(id2);
				if((id1_boolean==true)&&(id2_boolean==false))
				{
					deviceBandwidth[mdq]+=CommunicationSet[c].C2TCommunicationCost*CommunicationSet[c].CommunicationProbability;
					deviceNetworkLatency[mdq]=deviceNetworkLatency[mdq]+md[mdq].RTT*deviceBandwidth[mdq]*Bandwidth_Latency_Factor+cloudConsumption_div[mdq]*Cloud_Latency_Factor+deviceConsumption[mdq]*Device_Latency_Factor;
				}
				if((id1_boolean==false)&&(id2_boolean==true))
				{
					deviceBandwidth[mdq]+=CommunicationSet[c].T2CCommunicationCost*CommunicationSet[c].CommunicationProbability;
					deviceNetworkLatency[mdq]=deviceNetworkLatency[mdq]+md[mdq].RTT*deviceBandwidth[mdq]*Bandwidth_Latency_Factor+cloudConsumption_div[mdq]*Cloud_Latency_Factor+deviceConsumption[mdq]*Device_Latency_Factor;
				}
				/*
				if(id1_boolean!=id2_boolean)
				{
					deviceBandwidth[mdq]+=CommunicationSet[c].CommunicationBandwidth;
					deviceNetworkLatency[mdq]=deviceNetworkLatency[mdq]+md[mdq].RTT*deviceBandwidth[mdq]*Bandwidth_Latency_Factor+cloudConsumption_div[mdq]*Cloud_Latency_Factor+deviceConsumption[mdq]*Device_Latency_Factor;
				}
				*/	
				
			}
			
			for(int cq=0; cq< ComponentQuantity; cq++)
			{
				if(s.get(cq+mdq*ComponentQuantity)==false)
				{
					Component c=g.ComponentSet[cq];
					deviceBandwidth[mdq]+=c.MigrationCost;
				}
			}
			
			//System.out.println("Device ["+mdq+"] bandwidth: "+deviceBandwidth[mdq]+" latency: "+deviceNetworkLatency[mdq]);
		}
		
		deviceNetwork=new DeviceNetwork(deviceBandwidth,deviceNetworkLatency);

		return deviceNetwork;
	}
	
	public float[] overallCost(float[] deviceConsumption, float[] cloudConsumption_div,float[] deviceBandwidth, float[] migrationCost)
	{
		float overallCost[]=new float[MobileDeviceQuantity];
		
		for(int i=0; i< MobileDeviceQuantity; i++)
		{
			overallCost[i]=deviceConsumption[i]+cloudConsumption_div[i]+deviceBandwidth[i]+migrationCost[i];
		}
		
		return overallCost;
	}

}
