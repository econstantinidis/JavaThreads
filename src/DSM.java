import java.util.concurrent.ArrayBlockingQueue;

public class DSM extends Thread {
    
    private LocalMemory localMemory;
    private BroadcastAgent broadcastAgent;
    private ArrayBlockingQueue<Message<String, Object>> storeQueue; 
    
    /**
     * 
     * @param localMemory       The local cache created by the CPU
     * @param broadcastAgent    The agent created by the BroadcastSystem
     */
    protected DSM(LocalMemory localMemory, BroadcastAgent broadcastAgent)
    {
        this.localMemory = localMemory;
        this.broadcastAgent = broadcastAgent;
        this.storeQueue = new ArrayBlockingQueue<Message<String, Object>>(1000);
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
            this.storeQueue.put(message);
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
        Message<String, Object> message = storeQueue.poll();
        if(message != null)
        {
            //Poll the queue
            localMemory.store(message.getKey(), message.getValue());
            
            //Perform the store and broadcast
            localMemory.store(message.getKey(), message.getValue());
            broadcastAgent.broadcast(message);
        }
    }
}
