import java.util.concurrent.ArrayBlockingQueue;

public class DSM extends Thread {
    
    private LocalMemory localMemory;
    private BroadcastAgent broadcastAgent;
    protected ArrayBlockingQueue<Message<String, Object>> commandQueue; 
    protected Lock loadLock;
    /**
     * 
     * @param localMemory       The local cache created by the CPU
     * @param broadcastAgent    The agent created by the BroadcastSystem
     */
    protected DSM(LocalMemory localMemory, BroadcastAgent broadcastAgent)
    {
        this.localMemory = localMemory;
        this.broadcastAgent = broadcastAgent;
        this.commandQueue = new ArrayBlockingQueue<Message<String, Object>>(1000);
    }
    
    protected void setLoadLock(Lock loadLock)
    {
        this.loadLock = loadLock;
    }
    
    
    /**
     * The associated broadcast agent will add this store to the BroadcastSystem action queue
     * @param key       New keys can be added but the value of existing keys will be overwritten 
     * @param value     This parameter will be auto-boxed as an Object 
     */
    protected void store(String key, Object value)
    {
        //Create a new message via the arguments
        Message<String, Object> message = new Message<String, Object>(key, value);
        try
        {
            //Add the message to the store queue to be processed
            this.commandQueue.put(message);
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Load the Message from 
     * @param key       This parameter is used to retrieve the associated value
     * @return          The associated value is unboxed to the specified type by cast or is null.
     */
    @SuppressWarnings("unchecked")
    protected <T> T load(String key)
    {
        return (T) localMemory.load(key);
    }
    
    @Override
    public void run()
    { 
        while(true)
        {
            Message<String, Object> message = commandQueue.poll();
            if(message != null)
            {
                switch (message.opcode)
                {
                    case loadRequestDSM:
                        loadRequest(message);
                        break;
                    case loadExecuteDSM:
                        loadExecute(message);
                        break;
                    case storeDSM:
                        storeExecute(message);
                        break;
                    case storeBroadcastDSM:
                        storeBroadcast(message);
                        break;
                   default:
                       System.out.println("Invalid opcode in BroadcastAgent");
                }
            }
            
        }
    }
    
    protected void loadRequest(Message<String, Object> message)
    {
        message.configure(OPCODE.loadRequestBA, this);
        try 
        {
            broadcastAgent.commandQueue.put(message); //blocks if the queue is full
        } 
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    protected void loadExecute(Message<String, Object> message)
    {
        synchronized(loadLock)
        {
            loadLock.setReturn(localMemory.load(message.getKey()));
            loadLock.notify();
        }
    }
    
    protected void storeBroadcast(Message<String, Object> message)
    {
        message.configure(OPCODE.storeBroadcastBA, this);
        try 
        {
            localMemory.store(message.getKey(), message.getValue());
            broadcastAgent.commandQueue.put(message); //blocks if the queue is full
        } 
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    protected void storeExecute(Message<String, Object> message)
    {
        localMemory.store(message.getKey(), message.getValue());
    }

    public Object loadRAW(String key)
    {
        try {
            sleep(10);
        }
        catch(Exception e)
        {}
        return localMemory.load(key);
    }
}
