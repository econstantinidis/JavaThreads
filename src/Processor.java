
public class Processor extends Thread {
    protected final int cpuID;
    private DSM dsm;
    
    protected Processor(int cpuID, DSM dsm)
    {
        this.cpuID = cpuID;
        this.dsm = dsm;
    }
    
    @Override
    public void run()
    {
        //1. start dsm
        dsm.start();
        
        //2. Create and arrays
        int[] flag = new int[JavaThreads.testSize - 1];
        for(int i = 0; i < flag.length; i++)
        {
            flag[i] = -1;
        }
        
        int[] turn = new int[JavaThreads.testSize - 2];
        for(int i = 0; i < turn.length; i++)
        {
            turn[i] = -1;
        }
        
        //3. Execute algorithm
        while(true)
        {
            //<ENTRY SECTION>
            for(int k = 0; k <= JavaThreads.testSize-2; k++)
            {
                flag[cpuID] = k;
                turn[k] = cpuID;
                
                //Concurrency slide 66
                boolean exists = false;
                do
                {
                    exists = false;
                    for(int j = 0; j < JavaThreads.testSize; j++)
                    {
                        if(j != cpuID)
                        {
                            if(flag[j] >= k)
                            {
                                exists = true;
                            }
                        }
                    }
                    
                }while(exists && turn[k] == cpuID);
            }
                
            //<CRITICAL SECTION>
            try
            {
                System.out.println(String.format("Processor {0} entering the critical section!", cpuID));
                Thread.sleep(5000);
                System.out.println(String.format("Processor {0} leaving the critical section!", cpuID));
            }
            catch (InterruptedException e)
            {
                
                e.printStackTrace();
            }
            
            //<EXIT SECTION>
            flag[cpuID] = -1;
        }
    }

}
