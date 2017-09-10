import java.util.*;

//false = devices, true = cloud
public class BSolution {

	int ComponentQuantity;
	int MobileDeviceQuantity;
	int CellQuantity;
	int RandomCellQuantity;
	int CloudCellQuantity;
	//SolutionTable solutionTable;
	
	short[] solution;
	
	public BSolution(int _ComponentQuantity, int _MobileDeviceQuantity)
	{
		ComponentQuantity=_ComponentQuantity;
		MobileDeviceQuantity=_MobileDeviceQuantity;
		CellQuantity=ComponentQuantity*MobileDeviceQuantity;
		CloudCellQuantity=ComponentQuantity-2;
		RandomCellQuantity=ComponentQuantity-3;
		
		solution = new short[MobileDeviceQuantity];
		
	}
	
	public BSolution(int _ComponentQuantity, int _MobileDeviceQuantity, short[] _shorts)
	{
		ComponentQuantity=_ComponentQuantity;
		MobileDeviceQuantity=_MobileDeviceQuantity;
		CellQuantity=ComponentQuantity*MobileDeviceQuantity;
		CloudCellQuantity=ComponentQuantity-2;
		RandomCellQuantity=ComponentQuantity-3;
		
		solution = _shorts;
		
		
	}
	
	public static void main(String args[])
	{
		int ComponentQuantity=7;
		int MobileDeviceQuantity =2;
		
		BSolution cs = new BSolution(ComponentQuantity,MobileDeviceQuantity);
		cs.cloudSolution();
		
		
		short[] nums = new short[MobileDeviceQuantity];
		for(int k=0;k<MobileDeviceQuantity;k++)
		{
			nums[k]=1;
		}
		BSolution s = new BSolution(ComponentQuantity,MobileDeviceQuantity,nums);
		
		for(int i=0; i<MobileDeviceQuantity; i++)
		{
			System.out.println(cs.queryShortBit(s.solution[i],6));
		}
		
		s.toBinaryString();


	}
		
	public void cloudSolution()
	{ 
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<CloudCellQuantity; i++)
		{
			builder.append('1');
		}
		String binaryString=builder.toString();		
		short cloudShort=(short)Integer.parseInt(binaryString, 2);
		
		for(int i=0; i<MobileDeviceQuantity; i++)
		{
			this.solution[i]=cloudShort;	
		}
	}
	
	//for a solution, get the true/false value. index stands for the i+ MobileDeviceID*ComponentQuantity, where MobileDeviceID starts from 0
	public Boolean queryBit(int index)
	{
		int DeviceID = index/ComponentQuantity;
		int ComponentID = index % ComponentQuantity;
		
		return queryShortBit(solution[DeviceID],ComponentID);
	}
	
	public Boolean queryShortBit(short bitNum, int bitIndex)
	{
		//bitIndex--;
		
		String binaryString = Integer.toBinaryString(bitNum);
		int bitDiff = ComponentQuantity - binaryString.length();
		if(bitDiff > 0)
		{
			StringBuilder builder = new StringBuilder();
			for(int i=0; i<bitDiff; i++)
			{
				builder.append('0');
			}
			builder.append(binaryString);
			binaryString=builder.toString();
			//System.out.print(binaryString);
		}
		if(binaryString.charAt(bitIndex)=='1')
		{
			return true;
		}
		else
		{
			return false;
		}		
	}
	
	public char queryBitChar(int index)
	{
		int DeviceID = index/ComponentQuantity;
		int ComponentID = index % ComponentQuantity;
		
		return queryShortBitChar(solution[DeviceID],ComponentID);
	}
	
	public char queryShortBitChar(short bitNum, int bitIndex)
	{
		//bitIndex--;
		
		String binaryString = Integer.toBinaryString(bitNum);
		int bitDiff = ComponentQuantity - binaryString.length();
		if(bitDiff > 0)
		{
			StringBuilder builder = new StringBuilder();
			for(int i=0; i<bitDiff; i++)
			{
				builder.append('0');
			}
			builder.append(binaryString);
			binaryString=builder.toString();
			//System.out.print(binaryString);
		}
		
		return binaryString.charAt(bitIndex);	
	}
	
	
	
	public String toBinaryString()
	{
		StringBuilder builder =new StringBuilder();
		
		for(int i=0; i<MobileDeviceQuantity; i++)
		{
			
			for(int j=0; j< ComponentQuantity; j++)
			{
				builder.append(queryShortBitChar(solution[i],j));
				//System.out.print(queryShortBitChar(solution[i],j));
			}
			
			//builder.append(Integer.toBinaryString(solution[i]));
			//System.out.print(Integer.toBinaryString(solution[i]));
			//builder.append("-");
			//System.out.print("-");
		}
		
		return builder.toString();
		
	}
		
}
