import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;


public class P1 {

	public static void main(String args[])
	{
		
		//parameters
		int SimulationTimes=1;
		int MobileDeviceQuantityStart = 1;
		int MobileDeviceQuantity=5;
		int maxMobileDeviceQuantity=5;
		
		printResult(SimulationTimes,MobileDeviceQuantityStart,MobileDeviceQuantity,maxMobileDeviceQuantity);
	}
	
	public static void printResult(int SimulationTimes, int MobileDeviceQuantityStart, int MobileDeviceQuantity, int maxMobileDeviceQuantity)
	{
		int element=18;
		
		
		//parameters
		
		System.out.println("cloud consumption oriented optimization");
		
		for(MobileDeviceQuantity=MobileDeviceQuantityStart;MobileDeviceQuantity<=maxMobileDeviceQuantity;MobileDeviceQuantity++)
		{
			//System.out.println("\n"+MobileDeviceQuantity+": ");
			//System.out.println("Cloud Con\t Cloud Band\t Dev. Con\t Dev. Lat\t Dev. Band");
			
			averageResult(element, MobileDeviceQuantity, SimulationTimes,"cloudconsumption");
			
			System.out.println();
		}
		
		
		System.out.println("device throughput oriented optimization");
		
		for(MobileDeviceQuantity=MobileDeviceQuantityStart;MobileDeviceQuantity<=maxMobileDeviceQuantity;MobileDeviceQuantity++)
		{
			//System.out.println("\n"+MobileDeviceQuantity+": ");
			//System.out.println("Cloud Con\t Cloud Band\t Dev. Con\t Dev. Lat\t Dev. Band");
			
			averageResult(element, MobileDeviceQuantity, SimulationTimes,"throughput");
			
			System.out.println();
		}
		
		System.out.println("overall cost optimization");
		
		for(MobileDeviceQuantity=MobileDeviceQuantityStart;MobileDeviceQuantity<=maxMobileDeviceQuantity;MobileDeviceQuantity++)
		{
			//System.out.println("\n"+MobileDeviceQuantity+": ");
			//System.out.println("Cloud Con\t Cloud Band\t Dev. Con\t Dev. Lat\t Dev. Band");
			
			averageResult(element, MobileDeviceQuantity, SimulationTimes,"overallcost");
			
			System.out.println();
		}
	}
	
	public static float[] averageResult(int element, int MobileDeviceQuantity, int SimulationTimes, String filename)
	{
		
		float[] accumulateResult=new float[element];
		float[] averageResult=new float[element];
        for(int i=0;i<element;i++)
        {
        	accumulateResult[i]=0;
        	averageResult[i]=0;
        }
		
        try{
			
			File f=new File("results/"+filename+ MobileDeviceQuantity +".txt");
	        if(!f.exists())
	        {
	            System.out.println("Could not find "+f.getAbsolutePath()+"!");
	        }
	        else
	        {
	        	 FileInputStream fis=new FileInputStream(f);
	        	 DataInputStream dis=new DataInputStream(fis);
	        	
	        	 float[] result=new float[element];
	        	
	        	 for(int i=0; i<SimulationTimes; i++)
	        	 {
					for(int j=0;j<element;j++)
					{
						result[j]=dis.readFloat();
						//float[] result=readFromFile(10,MobileDeviceQuantity);
						accumulateResult[j]=accumulateResult[j]+result[j];
					}
	        	 }
	        }
        }
        catch(Exception e)
		{
		}
        
		for(int i=0; i<element; i++)
		{
			averageResult[i]=accumulateResult[i]/SimulationTimes;
		}
		
        for(int i=0;i<element;i++)
        {
        	System.out.printf("%.2f\t", averageResult[i]);
        	//System.out.print(averageResult[i]+"\t");
        }
        
        return averageResult;
	}
	
	
	
	public static float[] readFromFile(int element, int MobileDeviceQuantity, String filename)
	{
		
		float[] result=new float[element];
		
		try{
			
			File f=new File("results/"+filename+ MobileDeviceQuantity +".txt");
	        if(!f.exists())
	        {
	            System.out.println("Could not find "+f.getAbsolutePath()+"!");
	        }
	        else
	        {
	            FileInputStream fis=new FileInputStream(f);
	            DataInputStream dis=new DataInputStream(fis);
	            
	            for(int i=0;i<element;i++)
	            {
	            	result[i]=dis.readFloat();
	            }
	            dis.close();
	            fis.close();

	        }
		}catch(Exception e)
		{
			
		}
		
        /*
        for(int i=0;i<n;i++)
        {
        	System.out.println(result[i]+" ");
        }
        */
		
		return result;
	}
}
