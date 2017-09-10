import java.io.*;
import java.util.*;

public class Experiments {

	public static final int INFINITE=999999999;
	public static final String GENEFILENAME = "gene";
	
	public static void main (String args[])
	{
				
		//********************************parameters**************************************//
		
		float Cloud_Latency_Factor=0.04f;
		float Device_Latency_Factor=0.035f;
		float Bandwidth_Latency_Factor=0.4f;
		float[] factors = {Cloud_Latency_Factor,Device_Latency_Factor,Bandwidth_Latency_Factor};
		
		//***********************************Game*****************************************//
		int ComponentQuantity=7;
		Component ComponentSet[]=new Component[ComponentQuantity]; 
		//int ComponentConsumption[]={1,20,30,27,50,38,15};
		double CloudExecutionCost[]={1,20,30,27,50,38,15};
		double TerminalExecutionCost[]={1.4,12,24,19,33,19,9};
		double MigrationCost[]={1,2,3,2.7,5,3.8,1.5};
		double ExecutionProbability[]={0.1,0.1,0.15,0.2,0.25,0.1,0.1};
		int CommunicationQuantity=7;
		ComponentCommunication CommunicationSet[]=new ComponentCommunication[CommunicationQuantity];
		//initiate components
		for(int i=0;i<ComponentQuantity;i++)
		{
			ComponentSet[i]=new Component(i,CloudExecutionCost[i],TerminalExecutionCost[i], MigrationCost[i], ExecutionProbability[i]);
		}
		//initiate communication between components
		CommunicationSet[0]=new ComponentCommunication(0,5,0.2,0.3,10,11,0.12);
		CommunicationSet[1]=new ComponentCommunication(4,5,0.1,0.2,8,9,0.13);
		CommunicationSet[2]=new ComponentCommunication(1,4,0.3,0.37,25,27,0.25);
		CommunicationSet[3]=new ComponentCommunication(3,5,0.05,0.08,4,5,0.1);
		CommunicationSet[4]=new ComponentCommunication(2,5,0.13,0.23,9,10, 0.1);
		CommunicationSet[5]=new ComponentCommunication(2,6,0.3,0.37,25,27,0.2);
		CommunicationSet[6]=new ComponentCommunication(3,6,0.01,0.03,2.6,3,0.1);
		
		//*********************************Cloud Server************************************//
		int CloudServerResource=100000;
		int CloudServerBandwidth=10000;
		
		//*********************************Mobile Device***********************************//
		int RTT= 10;
		int AvailableBandwidth=80;
		int AvailableResources=80;
		int TolerateLatency=120;
		int MobileDeviceQuantityStart = 1;
		int MobileDeviceQuantity=5;
		int maxMobileDeviceQuantity=5;
		
		//*******************************Simulation Times**********************************//
		int SimulationTimes=1;
		
		//*********************************GA parameters***********************************//
		int GA_SPACE_SIZE=500;
		int GA_ITERATION=50;
		int GA_EXPANDING_FACTOR=2;
		double GA_MUTATION_RATIO=0.2;
		double GA_CROSSOVER_RATIO=0.3;
		
		//********************************end parameters***********************************//


		//********************************Simulation Start*********************************//
		System.out.println("=========================Simulation Start=========================");
		
		//********************************Create the Game**********************************//
		Game g=new Game(ComponentQuantity, ComponentSet, CommunicationQuantity, CommunicationSet);
		//g.showInfo();
		
		//********************************Create the Cloud Server**************************//
		CloudServer cs=new CloudServer(CloudServerBandwidth,CloudServerResource);
		//cs.showInfo();
		
		
		//********************************Simulation Start*********************************//
		
		System.out.println("=========================Cloud-Consumption Optimization=========================");
		
		for(MobileDeviceQuantity=MobileDeviceQuantityStart; MobileDeviceQuantity<=maxMobileDeviceQuantity; MobileDeviceQuantity++)
		{
			
			//********************************Create the Solution Set**************************//
			//!!!!!!!!!!Solutions s=new Solutions(ComponentQuantity,MobileDeviceQuantity,solutionTable);
			//s.cloudSolutionInfo();
			//s.allSolutionInfo();
			
			for(int simu_time=0; simu_time<SimulationTimes; simu_time++)
			{
				
				//***********************Create the Set of Mobile Devices**************************//
				MobileDevice[] md=new MobileDevice[MobileDeviceQuantity]; 
				for(int i=0;i<MobileDeviceQuantity;i++)
				{
					int randomRTT=(int)(Math.random()*15);
					int randomAvailableBandwidth=(int)(Math.random()*40);
					int randomAvailableResources=(int)(Math.random()*80);
					//int randomTolerateLatency=(int)(Math.random()*24);
					int randomTolerateLatency=0;
					
					////////////////!!!!!!!!!! random stop
					randomRTT=15;
					randomAvailableBandwidth=40;
					randomAvailableResources=80;
					////////////////
					
					md[i]=new MobileDevice(i,RTT+randomRTT,AvailableBandwidth+randomAvailableBandwidth,AvailableResources+randomAvailableResources,TolerateLatency+randomTolerateLatency);
					//md[i].showInfo();
				}
				
				
				//********************************Cloud Mode Start*********************************//
				System.out.println("==========================All Cloud Mode==========================");
				//ArrayList<Boolean> solution=s.cloudSolution();
				ArrayList<Boolean> solution=cloudSolution(ComponentQuantity, MobileDeviceQuantity);
				Simulation simu=new Simulation(g,cs,md, solution,factors);
				ValidResult allCloudValidResult=simu.validate();
				showState(allCloudValidResult);
				showDetailedState(allCloudValidResult);
				
				float minCloudConsumption=allCloudValidResult.cloudConsumption;
				float minCloudConsumption2=minCloudConsumption;
				ValidResult minValidResult = allCloudValidResult;
				ValidResult minValidResult2 = minValidResult;
				
				/* no valid
				float no_valid_minCloudConsumption=allCloudValidResult.cloudConsumption;
				ValidResult no_valid_minValidResult = allCloudValidResult;
				*/
				
				System.out.println("=========================All Solution Mode========================");
				try{
					File f=new File("genes_backup/"+GENEFILENAME + "C" +ComponentQuantity +"D" + MobileDeviceQuantity +".txt");
			        if(!f.exists())
			        {
			            System.out.println("Could not find "+f.getAbsolutePath()+"!");
			        }
			        else
			        {
			            FileInputStream fis=new FileInputStream(f);
			            DataInputStream dis=new DataInputStream(fis);
			    		
			            ArrayList<Boolean> aSolution;
			            int i=0;
			            
			            while(!(aSolution = readGeneFromFile(ComponentQuantity, MobileDeviceQuantity,dis)).isEmpty()){
			            	i++;
							Simulation asimu=new Simulation(g,cs,md,aSolution,factors);
							ValidResult avr=asimu.validate();
							//showState(avr);
							//System.out.println("===========");
							if((avr.valid)&&(avr.cloudConsumption<minCloudConsumption))
							{
								minCloudConsumption=avr.cloudConsumption;
								minValidResult=avr;
							}
							/* no valid
							if(avr.cloudConsumption<no_valid_minCloudConsumption)
							{
								no_valid_minCloudConsumption=avr.cloudConsumption;
								no_valid_minValidResult=avr;
							}
							*/
			            }
			            System.out.println(" - total gene read: "+i);
			            dis.close();
			            fis.close();
			        }
				}catch(Exception e)
				{
					System.out.println(" read all Genes exeception! ");
				}

				showState(minValidResult);
				showDetailedState(minValidResult);
				//showState(no_valid_minValidResult);
				
				System.out.println("=========================GA Mode========================");
								
				GAProcessor gap = new GAProcessor(ComponentQuantity, MobileDeviceQuantity,factors);
				ArrayList<ArrayList> GASolutions;
				GASolutions = gap.GASolutions(GA_SPACE_SIZE);
				
				for(int gat=1; gat<=GA_ITERATION; gat++)
				{	
					//System.out.println("~~~~~ GA Iteration: "+gat +" ~~~~~");
					GASolutions = gap.growGASolutions(GASolutions, GA_EXPANDING_FACTOR, GA_CROSSOVER_RATIO, GA_MUTATION_RATIO);
					
					//System.out.println("----- Population of grown genes: "+GASolutions.size() +" -----");
					GASolutions = gap.sortGASolutions(GASolutions, g, cs, md, minCloudConsumption2, minValidResult2, "CloudConsumption" );
					
					//System.out.println("----- Population of valid genes: "+GASolutions.size() +" -----");
					GASolutions = gap.selectGASolutions(GASolutions, GA_SPACE_SIZE);
					
					//System.out.println("----- Population of selected genes: "+GASolutions.size() +" -----");
				}
				
				ValidResult minGAValidResult;
				if(!GASolutions.isEmpty())
				{
					Simulation gasimu=new Simulation(g,cs,md,GASolutions.get(0),factors);
					System.out.print("best gene: "+GASolutions.get(0).toString()+"\n");
					
					ValidResult gavr=gasimu.validate();
					if(gavr.valid)
					{
						System.out.println("Final cloud consumption: " + gavr.cloudConsumption);
						minGAValidResult = gavr;
						showState(gavr);
						showDetailedState(gavr);
					}else
					{
						minGAValidResult = minValidResult2;
						System.out.println("No valid gene was found!");
					}
				}else
				{
					minGAValidResult = minValidResult2;
					System.out.println("No valid gene was retained!");
				}
				
				System.out.println("=========================End of Cloud-Consumption Optimization========================");
				
				saveToFile(allCloudValidResult, minValidResult, minGAValidResult, "cloudconsumption");
			}
		}
			
		
		System.out.println("=========================Throughput Optimization=========================");
		
		for(MobileDeviceQuantity=MobileDeviceQuantityStart; MobileDeviceQuantity<=maxMobileDeviceQuantity; MobileDeviceQuantity++)
		{
			
			//********************************Create the Solution Set**************************//		
			for(int simu_time=0; simu_time<SimulationTimes; simu_time++)
			{
				
				//***********************Create the Set of Mobile Devices**************************//
				MobileDevice[] md=new MobileDevice[MobileDeviceQuantity]; 
				for(int i=0;i<MobileDeviceQuantity;i++)
				{
					int randomRTT=(int)(Math.random()*15);
					int randomAvailableBandwidth=(int)(Math.random()*40);
					int randomAvailableResources=(int)(Math.random()*80);
					//int randomTolerateLatency=(int)(Math.random()*24);
					int randomTolerateLatency=0;
					
					md[i]=new MobileDevice(i,RTT+randomRTT,AvailableBandwidth+randomAvailableBandwidth,AvailableResources+randomAvailableResources,TolerateLatency+randomTolerateLatency);
					//md[i].showInfo();
				}
				
				//********************************Cloud Mode Start*********************************//
				System.out.println("==========================All Cloud Mode==========================");
				//ArrayList<Boolean> solution=s.cloudSolution();
				ArrayList<Boolean> solution=cloudSolution(ComponentQuantity, MobileDeviceQuantity);
				Simulation simu=new Simulation(g,cs,md,solution,factors);
				ValidResult allCloudValidResult=simu.validate();
				showState(allCloudValidResult);
				showDetailedState(allCloudValidResult);
				
				float minAverageDeviceBandwidth=allCloudValidResult.averageDeviceBandwidth;
				float minAverageDeviceBandwidth2 = minAverageDeviceBandwidth;
				ValidResult minValidResult = allCloudValidResult;
				ValidResult minValidResult2 = minValidResult;
				
				/* no valid
				float no_valid_minCloudConsumption=allCloudValidResult.cloudConsumption;
				ValidResult no_valid_minValidResult = allCloudValidResult;
				*/
				
				System.out.println("=========================All Solution Mode========================");
				try{
					File f=new File("genes_backup/"+GENEFILENAME + "C" +ComponentQuantity +"D" + MobileDeviceQuantity +".txt");
			        if(!f.exists())
			        {
			            System.out.println("Could not find "+f.getAbsolutePath()+"!");
			        }
			        else
			        {
			            FileInputStream fis=new FileInputStream(f);
			            DataInputStream dis=new DataInputStream(fis);
			    		
			            ArrayList<Boolean> aSolution;
			            int i=0;
			            
			            while(!(aSolution = readGeneFromFile(ComponentQuantity, MobileDeviceQuantity,dis)).isEmpty()){
			            	i++;
			            	Simulation asimu=new Simulation(g,cs,md,aSolution,factors);
							ValidResult avr=asimu.validate();
							//showState(avr);
							//System.out.println("===========");
							if((avr.valid)&&(avr.averageDeviceBandwidth<minAverageDeviceBandwidth))
							{
								minAverageDeviceBandwidth=avr.averageDeviceBandwidth;
								minValidResult=avr;
							}
			            }
			            System.out.println(" - total gene read: "+i);
			            dis.close();
			            fis.close();
			        }
				}catch(Exception e)
				{
					System.out.println(" read all Genes exeception! ");
				}
				showState(minValidResult);
				showDetailedState(minValidResult);
				//showState(no_valid_minValidResult);
				
				System.out.println("=========================GA Mode========================");
				
				GAProcessor gap = new GAProcessor(ComponentQuantity, MobileDeviceQuantity,factors);
				ArrayList<ArrayList> GASolutions;
				GASolutions = gap.GASolutions(GA_SPACE_SIZE);
				
					
				for(int gat=1; gat<=GA_ITERATION; gat++)
				{
					//System.out.println("~~~~~ GA Iteration: "+gat +" ~~~~~");
					GASolutions = gap.growGASolutions(GASolutions, GA_EXPANDING_FACTOR, GA_CROSSOVER_RATIO, GA_MUTATION_RATIO);
					
					//GASolutions = gap.sortGASolutionsByCloudConsumption(GASolutions, g, cs, md, minCloudConsumption2, minValidResult2 );
					GASolutions = gap.sortGASolutions(GASolutions, g, cs, md, minAverageDeviceBandwidth2, minValidResult2, "AverageDeviceBandwith" );
					
					//System.out.println("----- Population of valid genes: "+GASolutions.size() +" -----");					
					GASolutions = gap.selectGASolutions(GASolutions, GA_SPACE_SIZE);
					
					//System.out.println("----- Population of selected genes: "+GASolutions.size() +" -----");
				}
				
				
				ValidResult minGAValidResult;
				
				if(!GASolutions.isEmpty())
				{
					Simulation gasimu=new Simulation(g,cs,md,GASolutions.get(0),factors);
					ValidResult gavr=gasimu.validate();
					if(gavr.valid)
					{
						System.out.println("Final average device bandwidth: " + gavr.averageDeviceBandwidth);
						minGAValidResult = gavr;
						showState(gavr);
						showDetailedState(gavr);
					}else
					{
						minGAValidResult = minValidResult2;
						System.out.println("No valid gene was found!");
					}
				}else
				{
					minGAValidResult = minValidResult2;
					System.out.println("No valid gene was retained!");
				}
				
				System.out.println("=========================End of Throughput-Oriented Optimization========================");
				
				saveToFile(allCloudValidResult, minValidResult, minGAValidResult, "throughput");

			}
		}
		
		

		System.out.println("=========================Overall Cost Optimization=========================");
		
		for(MobileDeviceQuantity=MobileDeviceQuantityStart; MobileDeviceQuantity<=maxMobileDeviceQuantity; MobileDeviceQuantity++)
		{
			
			//********************************Create the Solution Set**************************//
			
			for(int simu_time=0; simu_time<SimulationTimes; simu_time++)
			{
				
				//***********************Create the Set of Mobile Devices**************************//
				MobileDevice[] md=new MobileDevice[MobileDeviceQuantity]; 
				for(int i=0;i<MobileDeviceQuantity;i++)
				{
					int randomRTT=(int)(Math.random()*15);
					int randomAvailableBandwidth=(int)(Math.random()*40);
					int randomAvailableResources=(int)(Math.random()*80);
					//int randomTolerateLatency=(int)(Math.random()*24);
					int randomTolerateLatency=0;
					
					md[i]=new MobileDevice(i,RTT+randomRTT,AvailableBandwidth+randomAvailableBandwidth,AvailableResources+randomAvailableResources,TolerateLatency+randomTolerateLatency);
					//md[i].showInfo();
				}
				
				//********************************Cloud Mode Start*********************************//
				System.out.println("==========================All Cloud Mode==========================");
				//ArrayList<Boolean> solution=s.cloudSolution();
				ArrayList<Boolean> solution=cloudSolution(ComponentQuantity, MobileDeviceQuantity);
				Simulation simu=new Simulation(g,cs,md,solution,factors);
				ValidResult allCloudValidResult=simu.validate();
				showState(allCloudValidResult);
				showDetailedState(allCloudValidResult);
				
				float minAverageDeviceOverallCost=allCloudValidResult.averageDeviceOverallCost;
				float minAverageDeviceOverallCost2 = minAverageDeviceOverallCost;
				ValidResult minValidResult = allCloudValidResult;
				ValidResult minValidResult2 = minValidResult;
				
				/* no valid
				float no_valid_minCloudConsumption=allCloudValidResult.cloudConsumption;
				ValidResult no_valid_minValidResult = allCloudValidResult;
				*/
				
				System.out.println("=========================All Solution Mode========================");
				
					
					
				//////
				try{
					File f=new File("genes_backup/"+GENEFILENAME + "C" +ComponentQuantity +"D" + MobileDeviceQuantity +".txt");
			        if(!f.exists())
			        {
			            System.out.println("Could not find "+f.getAbsolutePath()+"!");
			        }
			        else
			        {
			            FileInputStream fis=new FileInputStream(f);
			            DataInputStream dis=new DataInputStream(fis);
			    		
			            ArrayList<Boolean> aSolution;
			            int i=0;
			            while(!(aSolution = readGeneFromFile(ComponentQuantity, MobileDeviceQuantity,dis)).isEmpty()){
			            	i++;
			            	Simulation asimu=new Simulation(g,cs,md,aSolution,factors);
							ValidResult avr=asimu.validate();
							//showState(avr);
							//System.out.println("===========");
							if((avr.valid)&&(avr.averageDeviceOverallCost<minAverageDeviceOverallCost))
							{
								minAverageDeviceOverallCost=avr.averageDeviceOverallCost;
								minValidResult=avr;
							}
			            }
			            System.out.println(" - total gene read: "+i);
			            dis.close();
			            fis.close();
			        }
				}catch(Exception e)
				{
					System.out.println(" read all Genes exeception! ");
				}
				showState(minValidResult);
				showDetailedState(minValidResult);
				//showState(no_valid_minValidResult);
				
				System.out.println("=========================GA Mode========================");
				
				GAProcessor gap = new GAProcessor(ComponentQuantity, MobileDeviceQuantity,factors);
				ArrayList<ArrayList> GASolutions;
				GASolutions = gap.GASolutions(GA_SPACE_SIZE);
					
				for(int gat=1; gat<=GA_ITERATION; gat++)
				{
					//System.out.println("~~~~~ GA Iteration: "+gat +" ~~~~~");
					GASolutions = gap.growGASolutions(GASolutions, GA_EXPANDING_FACTOR, GA_CROSSOVER_RATIO, GA_MUTATION_RATIO);
					
					//System.out.println("----- Population of grown genes: "+GASolutions.size() +" -----");
					GASolutions = gap.sortGASolutions(GASolutions, g, cs, md, minAverageDeviceOverallCost2, minValidResult2, "AverageDeviceOverallCost" );
					
					//System.out.println("----- Population of valid genes: "+GASolutions.size() +" -----");
					GASolutions = gap.selectGASolutions(GASolutions, GA_SPACE_SIZE);
					
					//System.out.println("----- Population of selected genes: "+GASolutions.size() +" -----");
				}
				
				
				ValidResult minGAValidResult;
				
				if(!GASolutions.isEmpty())
				{
					Simulation gasimu=new Simulation(g,cs,md,GASolutions.get(0),factors);
					ValidResult gavr=gasimu.validate();
					if(gavr.valid)
					{
						System.out.println("Final average device overall cost: " + gavr.averageDeviceOverallCost);
						minGAValidResult = gavr;
						showState(gavr);
						showDetailedState(gavr);
					}else
					{
						minGAValidResult = minValidResult2;
						System.out.println("No valid gene was found!");
					}
				}else
				{
					minGAValidResult = minValidResult2;
					System.out.println("No valid gene was retained!");
				}
				
				System.out.println("=========================End of Overall Cost Optimization========================");
				
				saveToFile(allCloudValidResult, minValidResult, minGAValidResult, "overallcost");

			}
		}
		
		
		//********************************Simulation End***********************************//
		System.out.println("==========================Simulation End==========================");
		
		PrintResult.printResult(SimulationTimes,MobileDeviceQuantityStart,MobileDeviceQuantity,maxMobileDeviceQuantity);
	}
	
	public static void saveToFile(ValidResult allvr, ValidResult minvr, ValidResult gavr, String filename)
	{
		
		int MobileDeviceQuantity=allvr.deviceBandwidth.length;

		try{
//			File f=new File("D:" + File.separator + "results"+ File.separator + "Results"+ MobileDeviceQuantity +".txt");
			File f=new File("results/"+filename+ MobileDeviceQuantity +".txt");
			if(!f.exists())
			{
				f.createNewFile();
				//System.out.println("Could not find "+f.getPath()+"!");
			}
			FileOutputStream fos=new FileOutputStream(f, true);
			DataOutputStream dos=new DataOutputStream(fos);
			dos.writeFloat(allvr.cloudConsumption);
			dos.writeFloat(allvr.cloudBandwidth);
			dos.writeFloat(allvr.averageDeviceConsumption);
			dos.writeFloat(allvr.averageDeviceNetworkLatency);
			dos.writeFloat(allvr.averageDeviceBandwidth);
			dos.writeFloat(allvr.averageDeviceOverallCost);			
			dos.writeFloat(minvr.cloudConsumption);
			dos.writeFloat(minvr.cloudBandwidth);
			dos.writeFloat(minvr.averageDeviceConsumption);
			dos.writeFloat(minvr.averageDeviceNetworkLatency);
			dos.writeFloat(minvr.averageDeviceBandwidth);
			dos.writeFloat(minvr.averageDeviceOverallCost);			
			dos.writeFloat(gavr.cloudConsumption);
			dos.writeFloat(gavr.cloudBandwidth);
			dos.writeFloat(gavr.averageDeviceConsumption);
			dos.writeFloat(gavr.averageDeviceNetworkLatency);
			dos.writeFloat(gavr.averageDeviceBandwidth);
			dos.writeFloat(gavr.averageDeviceOverallCost);			
			System.out.println("Results saved to file.");
			dos.close();
			fos.close();
		}catch(Exception e)
		{
			
		}
	}
	
	/*
	public static void readFromFile(int n, String filename)
	{
		try{
			float[] result=new float[n];
			
	        File f=new File("D:" + File.separator + filename + File.separator + "CloudConsumption.txt");
	        if(!f.exists())
	        {
	            System.out.println("Could not find "+f.getAbsolutePath()+"!");
	        }
	        else
	        {
	            FileInputStream fis=new FileInputStream(f);//¥¥Ω®∂‘”¶fµƒŒƒº˛ ‰»Î¡˜
	            DataInputStream dis=new DataInputStream(fis);
	            
	            for(int i=0;i<n;i++)
	            {
	            	result[i]=dis.readFloat();
	            }
	            dis.close();
	            fis.close();

	            for(int i=0;i<n;i++)
	            {
	            	System.out.println(result[i]+" ");
	            }
	            
	            
	        }
		}catch(Exception e)
		{
			
		}
	}
	*/
	
	public static void showState(ValidResult vr)
	{

		System.out.println("Validation: "+vr.valid);
		System.out.println("Cloud Consumption: "+vr.cloudConsumption);
		System.out.println("Cloud Bandwidth: "+vr.cloudBandwidth);
		System.out.println("Average Device Consumption: "+vr.averageDeviceConsumption);
		System.out.println("Average Device Network Latency: "+vr.averageDeviceNetworkLatency);
		System.out.println("Average Device Bandwidth: "+vr.averageDeviceBandwidth);
		System.out.println("Average Device Overallcost: "+vr.averageDeviceOverallCost);		
	}
	
	public static void showDetailedState(ValidResult vr)
	{
		/*!!!!!!!!!!
		System.out.println("Device Consumption: ");
		for(int i=0; i<vr.deviceBandwidth.length; i++)
		{
			System.out.print(vr.deviceConsumption[i]+"\t ");
		}
		System.out.println("\nDevice Bandwidth: ");
		for(int i=0; i<vr.deviceBandwidth.length; i++)
		{
			System.out.print(vr.deviceBandwidth[i]+"\t ");
		}
		System.out.println("\nDevice Network Latency: ");
		for(int i=0; i<vr.deviceBandwidth.length; i++)
		{
			System.out.print(vr.deviceNetworkLatency[i]+"\t ");
		}
		System.out.println("\nOverall Cost: ");
		for(int i=0; i<vr.deviceBandwidth.length; i++)
		{
			System.out.print(vr.deviceOverallCost[i]+"\t ");
		}
		*/
		System.out.println("");
	}
	
	
	//gene generation related functions
	public static ArrayList<Boolean> cloudSolution(int ComponentQuantity, int MobileDeviceQuantity)
	{
		//int CellQuantity=ComponentQuantity*DeviceQuantity;
		ArrayList<Boolean> aSolution=new ArrayList<Boolean>();
		
		for(int i=0; i<MobileDeviceQuantity; i++)
		{
			for(int j=0; j<ComponentQuantity; j++)
			{
				if((j==0)||(j==1))
					aSolution.add(false);
				else
					aSolution.add(true);
			}
		}
		
		return aSolution;
	}
	
	public static void readAllGenesFromFile(int ComponentQuantity, int MobileDeviceQuantity, String geneFilename)
	{

		try{
			File f=new File("genes_backup/"+geneFilename + "C" +ComponentQuantity +"D" + MobileDeviceQuantity +".txt");
	        if(!f.exists())
	        {
	            System.out.println("Could not find "+f.getAbsolutePath()+"!");
	        }
	        else
	        {
	            FileInputStream fis=new FileInputStream(f);
	            DataInputStream dis=new DataInputStream(fis);
	    		
	            ArrayList<Boolean> getASolution;
	            int i=0;
	            
	            while(!(getASolution = readGeneFromFile(ComponentQuantity, MobileDeviceQuantity,dis)).isEmpty()){
	            	//result[i++]=stringSolution;
	                //System.out.println(stringSolution);
	            	i++;
	            }
	            
	            System.out.println(" - totalNum read: "+i);

	            dis.close();
	            fis.close();
	        }
		}catch(Exception e)
		{
			System.out.println(" read all Genes exeception! ");
		}

	}
	
	public static ArrayList<Boolean> readGeneFromFile(int ComponentQuantity, int MobileDeviceQuantity, DataInputStream dis)
	{
		ArrayList<Boolean> aSolution = new ArrayList<Boolean>();
		
		int RandomCellQuantity = ComponentQuantity-3;

	
		try{
		    String stringSolution = dis.readLine();
		    if(stringSolution!=null)
		    {
		    	for(int i=0; i<stringSolution.length(); i++)
		    	{
		    		if(stringSolution.charAt(i)=='0')
		    		{
		    			aSolution.add(false);
		    		}else
		    		{
		    			aSolution.add(true);
		    		}
		    	}
		    }
	
		    /*
		    System.out.print(stringSolution+" ==> ");
		    for(int i=0; i<aSolution.size(); i++)
	    	{
		    	System.out.print(aSolution.get(i)+" ");
	    	}
		    System.out.println(";");
			*/
		}catch(Exception e){System.out.println(" read aSolution exeception! ");}
	   
		return aSolution;
	}
	
}
