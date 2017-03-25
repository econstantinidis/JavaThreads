
public class PetersonsAlgorithm {

        private static int[] flag;
        private static int[] turn;
        private static int n;
        protected PetersonsAlgorithm(int n)
        {
            //Set n
            PetersonsAlgorithm.n = n;
            
            //initialize flag array
            flag = new int[n - 1];
            for(int i = 0; i < flag.length; i++)
            {
                flag[i] = -1;
            }
            
            //initialize turn array
            turn = new int[n-2];
            for(int i = 0; i < turn.length; i++)
            {
                turn[i] = -1;
            }
        }
        
        public static void testCrit(int cpuID)
        {
            //<ENTRY SECTION>
            for(int k = 0; k <= n-2; k++)
            {
                flag[cpuID] = k;
                turn[k] = cpuID;
                
                //Concurrency slide 66
                boolean exists = false;
                do
                {
                    exists = false;
                    for(int j = 0; j < n; j++)
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
