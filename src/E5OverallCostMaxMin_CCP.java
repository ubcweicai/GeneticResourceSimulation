import java.io.*;
import java.util.*;


public class E5OverallCostMaxMin_CCP {

	public static final int INFINITE=999999999;
	public static final String GENEFILENAME = "gene";
	public static final String gene_path="D:/Data/genes_backup/";
	
	public static void main (String args[])
	{
				
		//*************************************parameters*******************************************//
		
		//*******************************Simulation Times**********************************//
		int SimulationTimes=2000;
		
		//***********************************Game*****************************************//
		int ComponentQuantity=10;
		//Component Execution Cost
		double cec_a=1;
		double cec_b=50;
		//Terminal Execution Cost Factor
		double terminal_execution_cost_factor=1.05;
		//Component Execution Probability
		double ep_a=0.1;
		double ep_b=0.7;
		//Component Migration Cost
		double mc_a=1;
		double mc_b=10;
		
		//Communication Probability
		double cc_p=0.25;
		//Communication Probability
		double cp_a=0.01;
		double cp_b=0.3;
		//Communication Cost
		double c2t_a=0.1;
		double c2t_b=200;
		double T2C_C2T_factor=1.05;
		double C2C_C2T_factor=0.02;
		double T2T_C2T_factor=0.03;
		
		//Latency Factor
		float Cloud_Latency_Factor=0.001f;
		float Device_Latency_Factor=0.002f;
		float Bandwidth_Latency_Factor=0.005f;
		float[] factors = {Cloud_Latency_Factor,Device_Latency_Factor,Bandwidth_Latency_Factor};
		
		//*********************************Cloud Server************************************//
		int CloudServerResource=9000;
		int CloudServerBandwidth=12000;
				
		//*********************************Mobile Device***********************************//
		int RTT= 10;
		int randomRTT_range = 15;
		int AvailableBandwidth=200;
		int randomAvailableBandwidth_range = 300;
		int AvailableResources=100;
		int randomAvailableResources_range = 300;
		int TolerateLatency=120;
		
		int MobileDeviceQuantity=1;
		int MobileDeviceQuantityStart =10;
		int MobileDeviceQuantityStep =10;
		int maxMobileDeviceQuantity=100;

		
		//*********************************GA parameters***********************************//
		int GA_SPACE_SIZE=50;
		int GA_ITERATION=100;
		int GA_EXPANDING_FACTOR=2;
		double GA_MUTATION_RATIO=0.2;
		double GA_CROSSOVER_RATIO=0.9;
		
		
//		int GA_ITERATIN_START=100;
//		int GA_ITERATIN_STEP=100;
//		int GA_ITERATIN_END=400;
//		int ADDITIONAL_GA_ITERATION=1000;
		int GA_ITERATIN_START=1;
		int GA_ITERATIN_STEP=1;
		int GA_ITERATIN_END=4;
		int ADDITIONAL_GA_ITERATION=1;
		//*************************************end parameters****************************************//

		/*ArrayList deviceQuantity=new ArrayList();
		ArrayList terminal=new ArrayList();
		ArrayList delay=new ArrayList();
		ArrayList GA1=new ArrayList();
		ArrayList GA2=new ArrayList();
		ArrayList GA3=new ArrayList();*/

		for(double cc_p_i=1; cc_p_i<=10; cc_p_i++)
		{
			cc_p=cc_p_i/10;	
		for(ComponentQuantity=10; ComponentQuantity<=10; ComponentQuantity++)
		{

			for(int simu_time=0; simu_time<SimulationTimes; simu_time++)
			{
				
					int write_ComponentQuantity;
					double write_cc_p;
					String write_MinOverallCost;
					String write_MinOverallCost_Unlimited;
					String write_MaxOverallCost;
					String write_MaxOverallCost_Unlimited;

					
					//********************************Simulation Preparation*********************************//
								
					//********************************Create the Game**********************************//
								
					//initiate game components
					Component ComponentSet[]=new Component[ComponentQuantity];
					double CloudExecutionCost[]=getCloudExecutionCost(ComponentQuantity, cec_a, cec_b);
					double TerminalExecutionCost[]=getTerminalExecutionCost(CloudExecutionCost, terminal_execution_cost_factor);
					double ExecutionProbability[]=getExecutionProbability(ComponentQuantity, ep_a, ep_b);
					double MigrationCost[]=getMigrationCost(ComponentQuantity, mc_a, mc_b);
						
					for(int i=0;i<ComponentQuantity;i++)
					{
						ComponentSet[i]=new Component(i,CloudExecutionCost[i],TerminalExecutionCost[i], MigrationCost[i], ExecutionProbability[i]);
						//System.out.println(CloudExecutionCost[i]+"->"+TerminalExecutionCost[i]);
					}
			
								
					//initiate communication between components
					int[][] communicationConnectionMatrix=getConnectionMatrix(ComponentQuantity,cc_p);
					double[][] communicationProbabilityMatrix=getRandomMatrix(ComponentQuantity,cp_a,cp_b);
					double[][] C2TMatrix=getRandomMatrix(ComponentQuantity,c2t_a,c2t_b);
					double[][] T2CMatrix=getFactorizedMatrix(C2TMatrix,T2C_C2T_factor);
					double[][] C2CMatrix=getFactorizedMatrix(C2TMatrix,C2C_C2T_factor);
					double[][] T2TMatrix=getFactorizedMatrix(C2TMatrix,T2T_C2T_factor);
								
					int CommunicationQuantity=getCommunicationQuantity(communicationConnectionMatrix);
					ComponentCommunication CommunicationSet[]=new ComponentCommunication[CommunicationQuantity];
					int ID1[]=getCommunicationID1List(communicationConnectionMatrix, CommunicationQuantity);
					int ID2[]=getCommunicationID2List(communicationConnectionMatrix, CommunicationQuantity);
					double C2C[]=getMatrixElements(C2CMatrix, ID1, ID2);
					double T2T[]=getMatrixElements(T2TMatrix, ID1, ID2);
					double C2T[]=getMatrixElements(C2TMatrix, ID1, ID2);
					double T2C[]=getMatrixElements(T2CMatrix, ID1, ID2);
					double CommProbability[]=getMatrixElements(communicationProbabilityMatrix, ID1, ID2);
								
						
					for(int i=0;i<CommunicationQuantity;i++)
					{
						//(ID1, ID2, C2C, T2T, C2T,  T2C, Probability)
						CommunicationSet[i]=new ComponentCommunication(ID1[i], ID2[i], C2C[i], T2T[i], C2T[i], T2C[i], CommProbability[i]);
					}
								
					Game g=new Game(ComponentQuantity, ComponentSet, CommunicationQuantity, CommunicationSet);
					//g.showInfo();
								
								
					//********************************Create the Cloud Server**************************//
					CloudServer cs=new CloudServer(CloudServerBandwidth,CloudServerResource);
					//cs.showInfo();
								
					//***********************Create the Set of Mobile Devices**************************//
					MobileDevice[] md=new MobileDevice[MobileDeviceQuantity]; 
					for(int i=0;i<MobileDeviceQuantity;i++)
					{
						int randomRTT=(int)(Math.random()*randomRTT_range);
						int randomAvailableBandwidth=(int)(Math.random()*randomAvailableBandwidth_range);
						int randomAvailableResources=(int)(Math.random()*randomAvailableResources_range);
						//int randomTolerateLatency=(int)(Math.random()*24);
						int randomTolerateLatency=0;
								
						md[i]=new MobileDevice(i,RTT+randomRTT,AvailableBandwidth+randomAvailableBandwidth,AvailableResources+randomAvailableResources,TolerateLatency+randomTolerateLatency);
						//md[i].showInfo();
					}
					
					//********************************Simulation Start*********************************//
					System.out.println("=======Simulation Start=======ComponentQuantity: "+ComponentQuantity+"=======Simulation Time: "+(simu_time+1)+"=======");
					
					//deviceQuantity.add(MobileDeviceQuantity);
					write_ComponentQuantity=ComponentQuantity;
					write_cc_p=cc_p;
					
					
				
					System.out.println("=========================Overall Cost Optimization=========================");			
					
					
	
					System.out.println("=========================Overall Cost Minimization Mode========================");
					
					float minDeviceOverallCost=INFINITE;
					ValidResult minValidResult_OverallCost=null;
					
					try{
						File f=new File(gene_path+GENEFILENAME + "C" +ComponentQuantity +"D" + MobileDeviceQuantity +".txt");
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
								if((avr.valid)&&(avr.averageDeviceOverallCost<minDeviceOverallCost))
								{
									minDeviceOverallCost=avr.averageDeviceOverallCost;
									minValidResult_OverallCost=avr;
								}
				            }
				            //System.out.println(" - total gene read: "+i);
				            dis.close();
				            fis.close();
				        }
					}catch(Exception e)
					{
						System.out.println(" read all Genes exeception! ");
					}
					if(minValidResult_OverallCost!=null)
					{
						//delay.add(String.format("%.2f",minValidResult_Latency.averageDeviceNetworkLatency));
						write_MinOverallCost=String.format("%.2f",minValidResult_OverallCost.averageDeviceOverallCost);
						////showState(minValidResult_Latency);
						//showDetailedState(minValidResult_throughput);
					}else
					{
						//delay.add(INFINITE);
						write_MinOverallCost=String.valueOf(INFINITE);
						System.out.println(" no valid solution found! ");
					}				

					
					
					System.out.println("=========================Overall Cost Minimization Unlimited Mode========================");
					
					minDeviceOverallCost=INFINITE;
					minValidResult_OverallCost=null;
					
					try{
						File f=new File(gene_path+GENEFILENAME + "C" +ComponentQuantity +"D" + MobileDeviceQuantity +".txt");
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
								if(avr.averageDeviceOverallCost<minDeviceOverallCost)
								{
									minDeviceOverallCost=avr.averageDeviceOverallCost;
									minValidResult_OverallCost=avr;
								}
				            }
				            //System.out.println(" - total gene read: "+i);
				            dis.close();
				            fis.close();
				        }
					}catch(Exception e)
					{
						System.out.println(" read all Genes exeception! ");
					}
					if(minValidResult_OverallCost!=null)
					{
						//delay.add(String.format("%.2f",minValidResult_Latency.averageDeviceNetworkLatency));
						write_MinOverallCost_Unlimited=String.format("%.2f",minValidResult_OverallCost.averageDeviceOverallCost);
						////showState(minValidResult_Latency);
						//showDetailedState(minValidResult_throughput);
					}else
					{
						//delay.add(INFINITE);
						write_MinOverallCost_Unlimited=String.valueOf(INFINITE);
						System.out.println(" no valid solution found in unlimited!!!!!!!!!!!!!!!!!!!! ");
					}				

					
					System.out.println("=========================Overall Cost Maximization Mode========================");
					
					
					float maxDeviceOverallCost=0;
					ValidResult maxValidResult_OverallCost=null;
					
					try{
						File f=new File(gene_path+GENEFILENAME + "C" +ComponentQuantity +"D" + MobileDeviceQuantity +".txt");
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
								if((avr.valid)&&(avr.averageDeviceOverallCost>maxDeviceOverallCost))
								{
									maxDeviceOverallCost=avr.averageDeviceOverallCost;
									maxValidResult_OverallCost=avr;
								}
				            }
				            //System.out.println(" - total gene read: "+i);
				            dis.close();
				            fis.close();
				        }
					}catch(Exception e)
					{
						System.out.println(" read all Genes exeception! ");
					}
					if(maxValidResult_OverallCost!=null)
					{
						//delay.add(String.format("%.2f",minValidResult_Latency.averageDeviceNetworkLatency));
						write_MaxOverallCost=String.format("%.2f",maxValidResult_OverallCost.averageDeviceOverallCost);
						////showState(minValidResult_Latency);
						//showDetailedState(minValidResult_throughput);
					}else
					{
						//delay.add(INFINITE);
						write_MaxOverallCost=String.valueOf(INFINITE);
						System.out.println(" no valid solution found! ");
					}				
					
					System.out.println("=========================Overall Cost Maximization Unlimited Mode========================");
					
					maxDeviceOverallCost=0;
					maxValidResult_OverallCost=null;
					
					try{
						File f=new File(gene_path+GENEFILENAME + "C" +ComponentQuantity +"D" + MobileDeviceQuantity +".txt");
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
								if(avr.averageDeviceOverallCost>maxDeviceOverallCost)
								{
									maxDeviceOverallCost=avr.averageDeviceOverallCost;
									maxValidResult_OverallCost=avr;
								}
				            }
				            //System.out.println(" - total gene read: "+i);
				            dis.close();
				            fis.close();
				        }
					}catch(Exception e)
					{
						System.out.println(" read all Genes exeception! ");
					}
					if(maxValidResult_OverallCost!=null)
					{
						//delay.add(String.format("%.2f",minValidResult_Latency.averageDeviceNetworkLatency));
						write_MaxOverallCost_Unlimited=String.format("%.2f",maxValidResult_OverallCost.averageDeviceOverallCost);
						////showState(minValidResult_Latency);
						//showDetailedState(minValidResult_throughput);
					}else
					{
						//delay.add(INFINITE);
						write_MaxOverallCost_Unlimited=String.valueOf(INFINITE);
						System.out.println(" no valid solution found in unlimited!!!!!!!!!!!!!!!!!!!! ");
					}				
					
					
					System.out.print(write_MinOverallCost+",");
					System.out.print(write_MinOverallCost_Unlimited+",");
					System.out.print(write_MaxOverallCost+",");
					System.out.print(write_MaxOverallCost_Unlimited+",");
					System.out.print(write_cc_p+",");
					System.out.print(write_ComponentQuantity+"\n");
					
					saveMaxMinToFile(write_MinOverallCost, write_MinOverallCost_Unlimited, write_MaxOverallCost, write_MaxOverallCost_Unlimited, write_ComponentQuantity, write_cc_p, "maxmin_result_ccp_all");
			}
		}
		}
		/*
		for(int i=0; i<terminal.size(); i++)
		{
			System.out.print(terminal.get(i).toString()+",");
			System.out.print(delay.get(i).toString()+",");
			System.out.print(GA1.get(i).toString()+",");
			System.out.print(GA2.get(i).toString()+",");
			System.out.print(GA3.get(i).toString()+",");
			System.out.print(deviceQuantity.get(i).toString()+"\n");
		}
		*/
		
		
		
		//saveToFile(terminal, delay, GA1, GA2, GA3, deviceQuantity, "genetic_result");
		
		//********************************Simulation End***********************************//
		System.out.println("==========================Simulation End==========================");
		//System.out.println("Simulation Results:");
		//System.out.println("Terminal Minimization Mode: "+terminal);
		//System.out.println("Throughput Minimization Mode: "+throughput);
		//System.out.println("Cloud Minimization Mode: "+cloud);
		
	}
	
	public static void saveMaxMinToFile(String write_MinOverallCost, String write_MinOverallCost_Unlimited, String write_MaxOverallCost, String write_MaxOverallCost_Unlimited, int componentQuantity, double cc_p, String filename)
	{
		

		try{
			File f=new File("newresults/"+filename +".csv");
			if(!f.exists())
			{
				f.createNewFile();
				//System.out.println("Could not find "+f.getPath()+"!");
			}
			FileOutputStream fos=new FileOutputStream(f, true);
			DataOutputStream dos=new DataOutputStream(fos);
			
			dos.writeBytes(write_MinOverallCost+",");
			dos.writeBytes(write_MinOverallCost_Unlimited+",");
			dos.writeBytes(write_MaxOverallCost+",");
			dos.writeBytes(write_MaxOverallCost_Unlimited+",");
			dos.writeBytes(componentQuantity+",");
			dos.writeBytes(cc_p+"\r");
			
			System.out.println("A result saved to file.");
			dos.close();
			fos.close();
		}catch(Exception e)
		{
			System.out.println("Result saving error: "+e.toString());
		}
	}
	
	public static void saveAllToFile(float cloud, float network, float device, String filename, int componentQuantity)
	{
		

		try{
			File f=new File("newresults/"+filename + componentQuantity +".csv");
			if(!f.exists())
			{
				f.createNewFile();
				//System.out.println("Could not find "+f.getPath()+"!");
			}
			FileOutputStream fos=new FileOutputStream(f, true);
			DataOutputStream dos=new DataOutputStream(fos);
			
			dos.writeBytes(cloud+",");
			dos.writeBytes(network+",");
			dos.writeBytes(device+"\r");
						
			System.out.println("Results saved to file.");
			dos.close();
			fos.close();
		}catch(Exception e)
		{
			
		}
	}
	
	
	
	
	public static void saveToFile(ArrayList terminal, ArrayList delay, ArrayList GA1, ArrayList GA2, ArrayList GA3, ArrayList deviceQuantity, String filename)
	{
		

		try{
			File f=new File("newresults/"+filename +".csv");
			if(!f.exists())
			{
				f.createNewFile();
				//System.out.println("Could not find "+f.getPath()+"!");
			}
			FileOutputStream fos=new FileOutputStream(f, true);
			DataOutputStream dos=new DataOutputStream(fos);
			
			for(int i=0; i<terminal.size(); i++)
			{
				dos.writeBytes(terminal.get(i).toString()+",");
				dos.writeBytes(delay.get(i).toString()+",");
				dos.writeBytes(GA1.get(i).toString()+",");
				dos.writeBytes(GA2.get(i).toString()+",");
				dos.writeBytes(GA3.get(i).toString()+",");
				dos.writeBytes(deviceQuantity.get(i).toString()+"\r");
			}
			
			System.out.println("Results saved to file.");
			dos.close();
			fos.close();
		}catch(Exception e)
		{
			
		}
	}
	
	//Construction Functions
	public static double[] getCloudExecutionCost(int ComponentQuantity, double a, double b)
	{
		double[] cloudExecutionCost=new double[ComponentQuantity];	
		for(int i=0; i < ComponentQuantity; i++)
		{
			cloudExecutionCost[i]=U_rand(a,b);
			//System.out.println(cloudExecutionCost[i]);
		}		
		return cloudExecutionCost;
	}
	
	public static double[] getTerminalExecutionCost(double[] cloudExecutionCost, double factor)
	{
		double[] terminalExecutionCost=new double[cloudExecutionCost.length];	
		for(int i=0; i < cloudExecutionCost.length; i++)
		{
			terminalExecutionCost[i]=cloudExecutionCost[i]*factor;
			//System.out.println(terminalExecutionCost[i]);
		}		
		return terminalExecutionCost;
	}
	
	public static double[] getExecutionProbability(int ComponentQuantity, double a, double b)
	{
		double[] executionProbability=new double[ComponentQuantity];	
		for(int i=0; i < ComponentQuantity; i++)
		{
			executionProbability[i]=U_rand(a,b);
			//System.out.println(executionProbability[i]);
		}		
		return executionProbability;
	}
	
	public static double[] getMigrationCost(int ComponentQuantity, double a, double b)
	{
		double[] migrationCost=new double[ComponentQuantity];	
		for(int i=0; i < ComponentQuantity; i++)
		{
			migrationCost[i]=U_rand(a,b);
			//System.out.println(migrationCost[i]);
		}		
		return migrationCost;
	}
	
	public static int[][] getConnectionMatrix(int ComponentQuantity, double p)
	{
		int[][] matrix = new int[ComponentQuantity][ComponentQuantity];
		
		for(int i=0; i<ComponentQuantity;i++)
		{
			for(int j=0; j<ComponentQuantity;j++)
			{
				if(i!=j){
					if(U_rand(0,1)<p)
					{
						matrix[i][j]=1;
					}else
					{
						matrix[i][j]=0;
					}
				}else{
					matrix[i][j]=0;
				}
				//System.out.print(matrix[i][j]+"\t");
			}
			//System.out.println();
		}
		return matrix;
	}
	
	public static double[][] getRandomMatrix(int ComponentQuantity, double a, double b)
	{
		double[][] matrix = new double[ComponentQuantity][ComponentQuantity];
		
		for(int i=0; i<ComponentQuantity;i++)
		{
			for(int j=0; j<ComponentQuantity;j++)
			{
				if(i!=j){
					matrix[i][j]=U_rand(a,b);
				}else{
					matrix[i][j]=0;
				}
				//System.out.print(matrix[i][j]+"\t");
			}
			//System.out.println();
		}
		return matrix;
	}
	
	public static double[][] getFactorizedMatrix(double[][] oldMatrix, double factor)
	{
		double[][] matrix = new double[oldMatrix.length][oldMatrix.length];
		for(int i=0; i<oldMatrix.length;i++)
		{
			for(int j=0; j<oldMatrix.length;j++)
			{
					matrix[i][j]=oldMatrix[i][j]*factor;
			}
		}
		return matrix;
	}
	
	public static int getCommunicationQuantity(int[][] matrix)
	{
		int communicationQuantity=0;
		for(int i=0; i<matrix.length;i++)
		{
			for(int j=0; j<matrix.length;j++)
			{
				if(matrix[i][j]!=0)
				{
					communicationQuantity++;
				}
			}
		}
		return communicationQuantity;
	}
	
	public static int[] getCommunicationID1List(int[][] matrix, int CommunicationQuantity)
	{
		int ID1[]=new int[CommunicationQuantity];
		int k=0;
		for(int i=0; i<matrix.length;i++)
		{
			for(int j=0; j<matrix.length;j++)
			{
				if(matrix[i][j]!=0)
				{
					//System.out.println(i);
					ID1[k++]=i;
				}
			}
		}
		return ID1;
	}
	
	public static int[] getCommunicationID2List(int[][] matrix, int CommunicationQuantity)
	{
		int ID2[]=new int[CommunicationQuantity];
		int k=0;
		for(int i=0; i<matrix.length;i++)
		{
			for(int j=0; j<matrix.length;j++)
			{
				if(matrix[i][j]!=0)
				{
					//System.out.println(j);
					ID2[k++]=j;
				}
			}
		}
		return ID2;
	}
	
	public static double getDegreeVariance(int ComponentQuantity, int[] ID1, int[] ID2)
	{
		double degreeVariance=0;
		int[] degree = new int[ComponentQuantity];
		
		for(int i=0; i < ID1.length; i++)
		{
			degree[ID1[i]]++;
			degree[ID2[i]]++;
		}
		
		double degreeTotal=0;
		for(int i=0; i < ComponentQuantity; i++)
		{
			degreeTotal=degreeTotal+degree[i];
		}
		double degreeExpect=degreeTotal/ComponentQuantity;
		double degreePower=0;
		for(int i=0; i < ComponentQuantity; i++)
		{
			degreePower=degreePower+Math.pow((degree[i]-degreeExpect), 2);
		}
		degreeVariance=degreePower/ComponentQuantity;
		
		return degreeVariance;
	}
	
	public static double[] getMatrixElements(double[][] matrix, int[] ID1, int[] ID2)
	{
		double communicationCost[]=new double[ID1.length];
		int k=0;
		for(int i=0; i<ID1.length;i++)
		{
			communicationCost[i]=matrix[ID1[i]][ID2[i]];
			//System.out.print(communicationCost[i]+"\t");
		}
		//System.out.println();
		return communicationCost;
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
			File f=new File(gene_path+geneFilename + "C" +ComponentQuantity +"D" + MobileDeviceQuantity +".txt");
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
	
	//Poison Random
	public static double P_rand(double Lamda){
		 double x=0,b=1,c=Math.exp(-Lamda),u; 
		 do {
		  u=Math.random();
		  b *=u;
		  if(b>=c)
		   x++;
		  }while(b>=c);
		 return x;
	}
	
	
	public static double U_rand(double a, double b){
		
		return Math.random()*(b-a)+a;
		
	}

	public static double N_rand(double a, double b)
	{
		java.util.Random random=new java.util.Random();
		return Math.sqrt(b)*random.nextGaussian()+a;
	}
	
}
