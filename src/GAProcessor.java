import java.util.ArrayList;
import java.util.Iterator;


public class GAProcessor {

	public static final int INFINITE=999999999;
	int ComponentQuantity;
	int MobileDeviceQuantity;
	int CellQuantity;
	int RandomCellQuantity;
	float[] factors;
	
	public GAProcessor(int _ComponentQuantity, int _MobileDeviceQuantity, float[] _factors)
	{
		ComponentQuantity=_ComponentQuantity;
		MobileDeviceQuantity=_MobileDeviceQuantity;
		CellQuantity=ComponentQuantity*MobileDeviceQuantity;
		RandomCellQuantity=(ComponentQuantity-3)*MobileDeviceQuantity;
		factors=_factors;
	}
	
	public ArrayList<ArrayList> GASolutions(ArrayList<ArrayList> allSolutions, int GA_SPACE_SIZE)
	{
		ArrayList<ArrayList> GASolutions=new ArrayList<ArrayList>();
		//GASolutions=(ArrayList<ArrayList>)allSolutions.clone();
		
		for(int randomSelector=1;randomSelector<=GA_SPACE_SIZE;randomSelector++)
		{
			int randomIndex=(int)Math.floor(Math.random()*allSolutions.size());
			ArrayList randomSolution=allSolutions.get(randomIndex);
			GASolutions.add(randomSolution);
			//System.out.print("random index: "+randomIndex+"\n");
			//System.out.print("random gene: "+randomSolution.toString()+"\n");
		}
		
		//System.out.print("with all solution mode\n");
		
		return GASolutions;
	}
	
	public ArrayList<ArrayList> GASolutions(int GA_SPACE_SIZE)
	{
		//System.out.print("called GASolutions Function\n");

		ArrayList<ArrayList> GASolutions=new ArrayList<ArrayList>();
		ArrayList<ArrayList> randomSolutions=new ArrayList<ArrayList>();
		
		for(int gssi=0;gssi<GA_SPACE_SIZE;gssi++)
		{
			ArrayList<Boolean> solution= new ArrayList<Boolean>();
			for(int rcqi=0;rcqi<RandomCellQuantity;rcqi++)
			{
				double random_dice=Math.random();
				
				if(random_dice<0.5)
				{
					solution.add(true);
				}else
				{
					solution.add(false);
				}
			}
			randomSolutions.add(solution);
		}
		

		int testindex=0;
		Iterator i=randomSolutions.iterator();
		while(i.hasNext()){
		   ArrayList<Boolean> solution=(ArrayList<Boolean>)((ArrayList<Boolean>)i.next()).clone();
		   for(int m=0;m<MobileDeviceQuantity;m++)
		   {
				   solution.add(0+m*ComponentQuantity, false);
				   solution.add(1+m*ComponentQuantity, false);
				   solution.add(2+m*ComponentQuantity, true);
		   }
		   GASolutions.add(solution);
		   //System.out.print("random index: "+(testindex++)+"\t");
		   //System.out.print("random gene: "+solution.toString()+"\n");
		}
		
		//System.out.print("without all solution mode\n");
		
		return GASolutions;
	}
	
	public ArrayList<ArrayList> growGASolutions(ArrayList<ArrayList> GASolutions, int GA_EXPANDING_FACTOR, double GA_CROSSOVER_RATIO, double GA_MUTATION_RATIO)
	{
		ArrayList<ArrayList> grownGASolutions = new ArrayList<ArrayList>();
		
		int GA_SPACE_SIZE=GASolutions.size();
		
		for(int ef=0; ef<GA_EXPANDING_FACTOR; ef++ )
		{
			ArrayList<ArrayList> copyGASolutions= new ArrayList<ArrayList>();
			
			for(ArrayList<Boolean> aGASolution : GASolutions){
							
				ArrayList<Boolean> copySolution= new ArrayList<Boolean>();
				for(Boolean aBoolean : aGASolution)
				{
					copySolution.add(aBoolean);
				}
				
				copyGASolutions.add(copySolution);
	        }
			
			grownGASolutions.addAll(copyGASolutions);
		}
		
		/*
		//before crossover and mutation
		for(int count=0;count<grownGASolutions.size();count++)
		{
			System.out.println("["+count+"]: "+grownGASolutions.get(count));
		}
		*/
		
		//perform crossover
		for(int count=GA_SPACE_SIZE; count<grownGASolutions.size();count++)
		{
			//System.out.println("["+count+"] try crossover");
			double crossover_random_dice=Math.random();
			if(crossover_random_dice<GA_CROSSOVER_RATIO)
			{
				int toBeCrossovered=GA_SPACE_SIZE+(int)Math.floor(Math.random()*(grownGASolutions.size()-GA_SPACE_SIZE));
				ArrayList<Boolean> crossoverSolution = (ArrayList<Boolean>)grownGASolutions.get(count);
				ArrayList<Boolean> toBeCrossoverSolution = (ArrayList<Boolean>)grownGASolutions.get(toBeCrossovered);
				
				int crossoverSolutionLength=crossoverSolution.size();
				int crossoverPosition1=(int)Math.floor(Math.random()*crossoverSolutionLength);
				int crossoverPosition2=(int)Math.floor(Math.random()*crossoverSolutionLength);
				
				int startPoint, endPoint;
				if(crossoverPosition1>crossoverPosition2)
				{
					startPoint=crossoverPosition2;
					endPoint=crossoverPosition1;
				} else
				{
					startPoint=crossoverPosition1;
					endPoint=crossoverPosition2;
				}
				
				if(startPoint!=endPoint)
				{
					//start crossover
					for(int crossoverIndex=startPoint;crossoverIndex<=endPoint;crossoverIndex++)
					{
						Boolean tempValue=crossoverSolution.get(crossoverIndex);
						crossoverSolution.set(crossoverIndex, toBeCrossoverSolution.get(crossoverIndex));
						toBeCrossoverSolution.set(crossoverIndex, tempValue);
					}
					
					for(int m=0;m<MobileDeviceQuantity;m++)
					{
						crossoverSolution.set(0+m*ComponentQuantity, false);
						crossoverSolution.set(1+m*ComponentQuantity, false);
						crossoverSolution.set(2+m*ComponentQuantity, true);
						toBeCrossoverSolution.set(0+m*ComponentQuantity, false);
						toBeCrossoverSolution.set(1+m*ComponentQuantity, false);
						toBeCrossoverSolution.set(2+m*ComponentQuantity, true);
					}
					
				}
				
				//System.out.println("["+count+"] was crossovered with ["+toBeCrossovered+"] in: ("+startPoint+", "+endPoint+")");
			}
		}
		
		//perform mutation
		for(int count=GA_SPACE_SIZE;count<grownGASolutions.size();count++)
		{
			//System.out.println("["+count+"] try mutation");
			double mutation_random_dice=Math.random();
			if(mutation_random_dice<GA_MUTATION_RATIO)
			{
				//get random gene 
				ArrayList<Boolean> mutateSolution = (ArrayList<Boolean>)grownGASolutions.get(count);
				int mutateSolutionLength=mutateSolution.size();
				
				int mutatePosition=(int)Math.ceil(Math.random()*mutateSolutionLength);
				while(mutatePosition%CellQuantity<=2)
				{
					mutatePosition=(int)Math.ceil(Math.random()*mutateSolutionLength);
				}
				
				if(mutateSolution.get(mutatePosition)==false)
				{
					mutateSolution.set(mutatePosition, true);
				}else
				{
					mutateSolution.set(mutatePosition, false);
				}
				
				for(int m=0;m<MobileDeviceQuantity;m++)
				{
					mutateSolution.set(0+m*ComponentQuantity, false);
					mutateSolution.set(1+m*ComponentQuantity, false);
					mutateSolution.set(2+m*ComponentQuantity, true);
				}
				
				grownGASolutions.set(count, mutateSolution);
				
				//System.out.println("!!!!!!["+count+"] gene's ["+mutatePosition+"] was mutated!!!!!");
			}
		}
		
		/*
		//after crossover and mutation
		for(int count=0;count<grownGASolutions.size();count++)
		{
			System.out.println("["+count+"]: "+grownGASolutions.get(count));
		}
		*/
		
		//System.out.print("GASolutions have been grown\n");
		
		return grownGASolutions;
	}
	
	
	
	public ArrayList<ArrayList> sortGASolutionsByCloudConsumption(ArrayList<ArrayList> GASolutions,Game g, CloudServer cs, MobileDevice[] md, double minCloudConsumption2, ValidResult minValidResult2)
	{
		ArrayList<ArrayList> selectedGASolutions= new ArrayList<ArrayList>();
		
		//copied
		if(!GASolutions.isEmpty())
		{
			
			int populationSize = GASolutions.size();
			double[] cloudConsumptionArray =new double[populationSize];
			
			int selectedPopulation=0;
			Iterator GAIterator=GASolutions.iterator();
			while(GAIterator.hasNext()){
				
				ArrayList<Boolean> asolution=(ArrayList<Boolean>)GAIterator.next();
				//System.out.print("get random gene: "+asolution.toString()+"\n");
				Simulation asimu=new Simulation(g,cs,md,asolution,factors);
				ValidResult avr=asimu.validate();
				if(avr.valid)
				{
					selectedGASolutions.add(asolution);
					cloudConsumptionArray[selectedPopulation++]=avr.cloudConsumption;
					//System.out.println("["+(selectedPopulation-1)+"]: "+ cloudConsumptionArray[selectedPopulation-1]);
					//System.out.println("["+(count_index-1)+"]: "+ asolution.toString());
				}else
				{
					
					//cloudConsumptionArray[count_index++]=INFINITE;
					//System.out.println("["+(count_index-1)+"]: "+ cloudConsumptionArray[count_index-1]);
					//System.out.println("["+(count_index-1)+"]: "+ asolution.toString());
				}
				//showState(avr);
				//System.out.println("===========");
				
				
				if((avr.valid)&&(avr.cloudConsumption<minCloudConsumption2))
				{
					//System.out.println("!!!!!!!!!!!!Better Gene Found!!!!!!!!!!!!!!!");
					//System.out.println("minCloudConsumption2: "+minCloudConsumption2);
					minCloudConsumption2=avr.cloudConsumption;
					minValidResult2=avr;
					//System.out.println("minCloudConsumption2 after: "+minCloudConsumption2);
				}
				
			}
			
			//Sorting for the Genes
			for(int populationI=0;populationI<selectedPopulation-1;populationI++)
			{
				for(int populationJ=populationI+1; populationJ<selectedPopulation; populationJ++) {
					
					if(cloudConsumptionArray[populationI]>cloudConsumptionArray[populationJ]){ 
						
						double temp_cloudConsumption=cloudConsumptionArray[populationI]; 
						cloudConsumptionArray[populationI]=cloudConsumptionArray[populationJ]; 
						cloudConsumptionArray[populationJ]=temp_cloudConsumption;
						
						ArrayList<ArrayList> temp_GASolution = selectedGASolutions.get(populationI);
						selectedGASolutions.set(populationI, selectedGASolutions.get(populationJ));
						selectedGASolutions.set(populationJ, temp_GASolution);
						
					}
					
				}
			}
			
			/*
			for(int testI=0; testI<populationSize;testI++)
			{
				System.out.println("["+testI+"]: "+ cloudConsumptionArray[testI]);
				System.out.println("=["+testI+"]: "+ GASolutions.get(testI).toString());
			}
			*/
			
			//System.out.println("minCloudConsumption2 sorted: "+ cloudConsumptionArray[0]);
			
			//showState(minValidResult2);
			//showDetailedState(minValidResult2);
			//showState(no_valid_minValidResult);
		}
		//copied
		
		System.out.print("GASolutions have been sorted by cloud consumption\n");
		
		return selectedGASolutions;
	}
	
	
	public ArrayList<ArrayList> sortGASolutions(ArrayList<ArrayList> GASolutions, Game g, CloudServer cs, MobileDevice[] md, double minValue, ValidResult minValidResult2, String sortStandard)
	{
		ArrayList<ArrayList> selectedGASolutions= new ArrayList<ArrayList>();
		
		//copied
		if(!GASolutions.isEmpty())
		{
			
			int populationSize = GASolutions.size();
			double[] cloudConsumptionArray =new double[populationSize];
			double[] averageDeviceBandwidthArray =new double[populationSize];
			double[] averageDeviceCostArray =new double[populationSize];
			double[] averageDeviceOverallCostArray =new double[populationSize];
			double[] averageDeviceLatency =new double[populationSize];

			int selectedPopulation=0;
			Iterator GAIterator=GASolutions.iterator();
			
			while(GAIterator.hasNext()){
				
				ArrayList<Boolean> asolution=(ArrayList<Boolean>)GAIterator.next();
				//System.out.print("get random gene: "+asolution.toString()+"\n");
				Simulation asimu=new Simulation(g,cs,md,asolution,factors);
				ValidResult avr=asimu.validate();
				if(avr.valid)
				{
					selectedGASolutions.add(asolution);
					cloudConsumptionArray[selectedPopulation]=avr.cloudConsumption;
					averageDeviceBandwidthArray[selectedPopulation] =avr.averageDeviceBandwidth;
					averageDeviceCostArray[selectedPopulation]=avr.averageDeviceConsumption;
					averageDeviceLatency[selectedPopulation] =avr.averageDeviceNetworkLatency;
					averageDeviceOverallCostArray[selectedPopulation++] =avr.averageDeviceOverallCost;
					
					//System.out.println("["+(selectedPopulation-1)+"]: "+ cloudConsumptionArray[selectedPopulation-1]);
					//System.out.println("["+(count_index-1)+"]: "+ asolution.toString());
				}else
				{
					
					//cloudConsumptionArray[count_index++]=INFINITE;
					//System.out.println("["+(count_index-1)+"]: "+ cloudConsumptionArray[count_index-1]);
					//System.out.println("["+(count_index-1)+"]: "+ asolution.toString());
				}
				//showState(avr);
				//System.out.println("===========");
				
				if(sortStandard.equalsIgnoreCase("CloudConsumption"))
				{
					//System.out.println("Shorted by CloudConsumption");

					if((avr.valid)&&(avr.cloudConsumption<minValue))
					{
						//System.out.println("!!!!!!!!!!!!Better Gene Found!!!!!!!!!!!!!!!");
						//System.out.println("minCloudConsumption2: "+minCloudConsumption2);
						minValue=avr.cloudConsumption;
						minValidResult2=avr;
						//System.out.println("minCloudConsumption2 after: "+minCloudConsumption2);
					}
					
					//Sorting for the Genes (CloudConsumption)
					for(int populationI=0;populationI<selectedPopulation-1;populationI++)
					{
						for(int populationJ=populationI+1; populationJ<selectedPopulation; populationJ++) {
							
							if(cloudConsumptionArray[populationI]>cloudConsumptionArray[populationJ]){ 
								
								double temp_cloudConsumption=cloudConsumptionArray[populationI]; 
								cloudConsumptionArray[populationI]=cloudConsumptionArray[populationJ]; 
								cloudConsumptionArray[populationJ]=temp_cloudConsumption;
								
								ArrayList<ArrayList> temp_GASolution = selectedGASolutions.get(populationI);
								selectedGASolutions.set(populationI, selectedGASolutions.get(populationJ));
								selectedGASolutions.set(populationJ, temp_GASolution);
								
							}
							
						}
					}
					
				}else if(sortStandard.equalsIgnoreCase("AverageDeviceLatency"))
				{
					//System.out.println("Shorted by CloudConsumption");

					if((avr.valid)&&(avr.averageDeviceNetworkLatency<minValue))
					{
						//System.out.println("!!!!!!!!!!!!Better Gene Found!!!!!!!!!!!!!!!");
						//System.out.println("minCloudConsumption2: "+minCloudConsumption2);
						minValue=avr.averageDeviceNetworkLatency;
						minValidResult2=avr;
						//System.out.println("minCloudConsumption2 after: "+minCloudConsumption2);
					}
					
					//Sorting for the Genes (CloudConsumption)
					for(int populationI=0;populationI<selectedPopulation-1;populationI++)
					{
						for(int populationJ=populationI+1; populationJ<selectedPopulation; populationJ++) {
							
							if(averageDeviceLatency[populationI]>averageDeviceLatency[populationJ]){ 
								
								double temp_averageDeviceLatency=averageDeviceLatency[populationI]; 
								averageDeviceLatency[populationI]=averageDeviceLatency[populationJ]; 
								averageDeviceLatency[populationJ]=temp_averageDeviceLatency;
								
								ArrayList<ArrayList> temp_GASolution = selectedGASolutions.get(populationI);
								selectedGASolutions.set(populationI, selectedGASolutions.get(populationJ));
								selectedGASolutions.set(populationJ, temp_GASolution);
								
							}
							
						}
					}
					
				} else if(sortStandard.equalsIgnoreCase("AverageDeviceBandwith"))
				{
					//System.out.println("Shorted by AverageDeviceBandwith");

					if((avr.valid)&&(avr.averageDeviceBandwidth<minValue))
					{
						//System.out.println("!!!!!!!!!!!!Better Gene Found!!!!!!!!!!!!!!!");
						//System.out.println("minCloudConsumption2: "+minCloudConsumption2);
						minValue=avr.averageDeviceBandwidth;
						minValidResult2=avr;
						//System.out.println("minCloudConsumption2 after: "+minCloudConsumption2);
					}
					
					//Sorting for the Genes (AverageDeviceBandwidth)
					for(int populationI=0;populationI<selectedPopulation-1;populationI++)
					{
						for(int populationJ=populationI+1; populationJ<selectedPopulation; populationJ++) {
							
							if(averageDeviceBandwidthArray[populationI]>averageDeviceBandwidthArray[populationJ]){ 
								
								double temp_averageDeviceBandwidth=averageDeviceBandwidthArray[populationI]; 
								averageDeviceBandwidthArray[populationI]=averageDeviceBandwidthArray[populationJ]; 
								averageDeviceBandwidthArray[populationJ]=temp_averageDeviceBandwidth;
								
								ArrayList<ArrayList> temp_GASolution = selectedGASolutions.get(populationI);
								selectedGASolutions.set(populationI, selectedGASolutions.get(populationJ));
								selectedGASolutions.set(populationJ, temp_GASolution);	
							}
						}
					}
					
				} else if(sortStandard.equalsIgnoreCase("AverageDeviceOverallCost"))
				{
					//System.out.println("Shorted by AverageDeviceBandwith");

					if((avr.valid)&&(avr.averageDeviceOverallCost<minValue))
					{
						//System.out.println("!!!!!!!!!!!!Better Gene Found!!!!!!!!!!!!!!!");
						//System.out.println("minCloudConsumption2: "+minCloudConsumption2);
						minValue=avr.averageDeviceOverallCost;
						minValidResult2=avr;
						//System.out.println("minCloudConsumption2 after: "+minCloudConsumption2);
					}
					
					//Sorting for the Genes (AverageDeviceOverallCost)
					for(int populationI=0;populationI<selectedPopulation-1;populationI++)
					{
						for(int populationJ=populationI+1; populationJ<selectedPopulation; populationJ++) {
							
							if(averageDeviceOverallCostArray[populationI]>averageDeviceOverallCostArray[populationJ]){ 
								
								double temp_averageDeviceOverallCost=averageDeviceOverallCostArray[populationI]; 
								averageDeviceOverallCostArray[populationI]=averageDeviceOverallCostArray[populationJ]; 
								averageDeviceOverallCostArray[populationJ]=temp_averageDeviceOverallCost;
								
								ArrayList<ArrayList> temp_GASolution = selectedGASolutions.get(populationI);
								selectedGASolutions.set(populationI, selectedGASolutions.get(populationJ));
								selectedGASolutions.set(populationJ, temp_GASolution);	
							}
						}
					}
					
				} else if(sortStandard.equalsIgnoreCase("AverageDeviceCost"))
				{
					//System.out.println("Shorted by AverageDeviceBandwith");

					if((avr.valid)&&(avr.averageDeviceConsumption<minValue))
					{
						//System.out.println("!!!!!!!!!!!!Better Gene Found!!!!!!!!!!!!!!!");
						//System.out.println("minCloudConsumption2: "+minCloudConsumption2);
						minValue=avr.averageDeviceConsumption;
						minValidResult2=avr;
						//System.out.println("minCloudConsumption2 after: "+minCloudConsumption2);
					}
					
					//Sorting for the Genes (AverageDeviceOverallCost)
					for(int populationI=0;populationI<selectedPopulation-1;populationI++)
					{
						for(int populationJ=populationI+1; populationJ<selectedPopulation; populationJ++) {
							
							if(averageDeviceCostArray[populationI]>averageDeviceCostArray[populationJ]){ 
								
								double temp_averageDeviceCostArray=averageDeviceCostArray[populationI]; 
								averageDeviceCostArray[populationI]=averageDeviceCostArray[populationJ]; 
								averageDeviceCostArray[populationJ]=temp_averageDeviceCostArray;
								
								ArrayList<ArrayList> temp_GASolution = selectedGASolutions.get(populationI);
								selectedGASolutions.set(populationI, selectedGASolutions.get(populationJ));
								selectedGASolutions.set(populationJ, temp_GASolution);	
							}
						}
					}
					
				} else
				{
					System.out.println("!!!!!!!!!!!ERROR!!!!!!!!!!!!!NO SHORTING STANDARD FOUND!!!!!!!");
				}
				
				
			}
			
			
			
			/*
			for(int testI=0; testI<populationSize;testI++)
			{
				System.out.println("["+testI+"]: "+ cloudConsumptionArray[testI]);
				System.out.println("=["+testI+"]: "+ GASolutions.get(testI).toString());
			}
			*/
			
			//System.out.println("minCloudConsumption2 sorted: "+ cloudConsumptionArray[0]);
			
			//showState(minValidResult2);
			//showDetailedState(minValidResult2);
			//showState(no_valid_minValidResult);
		}
		//copied
		
		//System.out.print("GASolutions have been sorted by cloud consumption\n");
		
		return selectedGASolutions;
	}
	
	
	
	
	public ArrayList<ArrayList> selectGASolutions(ArrayList<ArrayList> GASolutions, int GA_SPACE_SIZE)
	{
		ArrayList<ArrayList> selectedGASolutions=new ArrayList<ArrayList>();
		
		int populationSize=GASolutions.size();
		
		if(populationSize==GA_SPACE_SIZE)
		{
			selectedGASolutions=GASolutions;
		}else if(populationSize>GA_SPACE_SIZE)
		{
			selectedGASolutions=new ArrayList<ArrayList>(GASolutions.subList(0, GA_SPACE_SIZE));
			
		}else
		{
			selectedGASolutions=(ArrayList<ArrayList>) GASolutions.clone();
			ArrayList<ArrayList> randomSolutions = this.GASolutions(GA_SPACE_SIZE-populationSize);
			selectedGASolutions.addAll(randomSolutions);
		}
		
		//System.out.print("GASolutions have been selected\n");
		
		return selectedGASolutions;
	}
	
	
	public static void showState(ValidResult vr)
	{

		System.out.println("Validation: "+vr.valid);
		System.out.println("Cloud Consumption: "+vr.cloudConsumption);
		System.out.println("Cloud Bandwidth: "+vr.cloudBandwidth);
		System.out.println("Average Device Consumption: "+vr.averageDeviceConsumption);
		System.out.println("Average Device Network Latency: "+vr.averageDeviceNetworkLatency);
		System.out.println("Average Device Bandwidth: "+vr.averageDeviceBandwidth);
		
	}
	
	public static void showDetailedState(ValidResult vr)
	{
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
		System.out.println("");
	}
	
	
}
