import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

//false = devices, true = cloud
public class BSolutions {

	public static final String gene_path="D:/Data/genes_backup/";

	int ComponentQuantity;
	int MobileDeviceQuantity;
	int CellQuantity;
	int RandomCellQuantity;
	int CloudCellQuantity;
	//SolutionTable solutionTable;
	
	public BSolutions(int _ComponentQuantity, int _MobileDeviceQuantity)
	{
		ComponentQuantity=_ComponentQuantity;
		MobileDeviceQuantity=_MobileDeviceQuantity;
		CellQuantity=ComponentQuantity*MobileDeviceQuantity;
		CloudCellQuantity=ComponentQuantity-2;
		RandomCellQuantity=ComponentQuantity-3;
	}
	
	public static void main(String args[])
	{
		int ComponentQuantity=6;
		int MobileDeviceQuantity = 1;
		
		for(ComponentQuantity=6;ComponentQuantity<=17;ComponentQuantity++)
			for(MobileDeviceQuantity=1;MobileDeviceQuantity<=1;MobileDeviceQuantity++)
			{
				BSolutions bs= new BSolutions(ComponentQuantity, MobileDeviceQuantity);
				bs.allSolutions();
			}
		System.out.println(" ~ Read ~ ");
		//readGenesFromFile(ComponentQuantity,MobileDeviceQuantity,"gene");
		//readAllGenesFromFile(ComponentQuantity,MobileDeviceQuantity,"gene");

	}


	public ArrayList<BSolution> allSolutions()
	{
		ArrayList<BSolution> allSolutions=new ArrayList<BSolution>();
		//create a all blank solution: zeroSolution
		short[] nums = new short[MobileDeviceQuantity];
		for(int k=0;k<MobileDeviceQuantity;k++)
		{
			nums[k]=(short)Math.pow(2, RandomCellQuantity);
		}
		BSolution zeroSolution = new BSolution(ComponentQuantity,MobileDeviceQuantity,nums);
		//set up binary carry algorithm to get all solutions
		int shortUpperBound = (int) (Math.pow(2, RandomCellQuantity)-1+Math.pow(2, RandomCellQuantity));
		int totalNum = (int) Math.pow(2, RandomCellQuantity*MobileDeviceQuantity);
		System.out.println(" - totalNum: "+totalNum);
		//System.out.println("shortUpperBound: " + shortUpperBound +" - totalNum: "+totalNum);
		//add the zeroSolution
		//allSolutions.add(zeroSolution);
		saveGeneToFile(zeroSolution.toBinaryString(),ComponentQuantity,MobileDeviceQuantity,"gene");
		System.out.println("saved gene [0] : " + zeroSolution.toBinaryString());
		//add all solutions
		for(int i=0; i<totalNum-1; i++)
		{
			zeroSolution.solution[0]=(short)(zeroSolution.solution[0]+1);
			int carry=1;
			while(carry==1)
			{
				carry=0;
				for(int j=0; j<MobileDeviceQuantity; j++)
				{
					if(zeroSolution.solution[j]>shortUpperBound)
					{
						zeroSolution.solution[j]=(short)Math.pow(2, RandomCellQuantity);
						if(j<MobileDeviceQuantity-1)
						{
							zeroSolution.solution[j+1]+=1;
						}
						carry=1;
					}
				}
				
			}
			//allSolutions.add(zeroSolution);
			saveGeneToFile(zeroSolution.toBinaryString(),ComponentQuantity,MobileDeviceQuantity,"gene");
			System.out.println("saved gene ["+(i+1)+"] : " + zeroSolution.toBinaryString());	
		}
		return allSolutions;
	}
	
	
	public static void saveGeneToFile(String solutionBinaryString, int ComponentQuantity, int MobileDeviceQuantity, String filename)
	{
		try{
			File f=new File(gene_path+filename + "C" +ComponentQuantity +"D" + MobileDeviceQuantity +".txt");
			if(!f.exists())
			{
				f.createNewFile();
				//System.out.println("Could not find "+f.getPath()+"!");
			}
			FileOutputStream fos=new FileOutputStream(f, true);
			DataOutputStream dos=new DataOutputStream(fos);
			dos.writeBytes(solutionBinaryString+"\r");			
			//System.out.println("Gene saved to file.");
			dos.close();
			fos.close();
		}catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	/*
	public static String[] readGenesFromFile(int ComponentQuantity, int MobileDeviceQuantity, String filename)
	{
		int RandomCellQuantity = ComponentQuantity-3;
		int solutionLength =ComponentQuantity*MobileDeviceQuantity;
		int element=(int) Math.pow(2, RandomCellQuantity*MobileDeviceQuantity);
		String[] result=new String[element];

		try{
			File f=new File("genes_backup/"+filename + "C" +ComponentQuantity +"D" + MobileDeviceQuantity +".txt");
	        if(!f.exists())
	        {
	            System.out.println("Could not find "+f.getAbsolutePath()+"!");
	        }
	        else
	        {
	            FileInputStream fis=new FileInputStream(f);
	            DataInputStream dis=new DataInputStream(fis);
	    		
	            String stringSolution;
	            int i=0;
	            
	            while((stringSolution = dis.readLine()) != null){
	            	result[i++]=stringSolution;
	                System.out.println(stringSolution);
	            }
	            
	            System.out.println(" - totalNum read: "+i);

	            dis.close();
	            fis.close();
	        }
		}catch(Exception e)
		{
			System.out.println(" exeception! ");
		}
		
		return result;
	}
	*/
	
	public static void readAllGenesFromFile(int ComponentQuantity, int MobileDeviceQuantity, String geneFilename)
	{

		try{
			File f=new File("../../../../Data/genes_backup/"+geneFilename + "C" +ComponentQuantity +"D" + MobileDeviceQuantity +".txt");
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

	    
	    System.out.print(stringSolution+" ==> ");
	    for(int i=0; i<aSolution.size(); i++)
    	{
	    	System.out.print(aSolution.get(i)+" ");
    	}
	    System.out.println(";");
		
	    
		}catch(Exception e){System.out.println(" read aSolution exeception! ");}
	   
		return aSolution;
	}
	
}
