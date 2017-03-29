import java.util.concurrent.ArrayBlockingQueue;

public class BroadcastAgent extends Thread {
    private ArrayBlockingQueue<Message<String, Object>> qBroadcast;
    private ArrayBlockingQueue<Message<String, Object>> qReceive;
    private LocalMemory localMemory;
    
    /**
     * Only the BroadcastSystem should be allowed to generate this class.
     * A new agent can be created via the createAgent function. In doing
     * so the appropriate communication channels are set up and registered.
     * @param qRecieve      The queue used to receive messages from the BroadcastSystem 
     * @param qBroadCast    The queue used to send messages to the BroadcastSystem
     */
    protected BroadcastAgent(ArrayBlockingQueue<Message<String, Object>> qReceive, ArrayBlockingQueue<Message<String, Object>> qBroadCast)
    {
        this.qBroadcast = qBroadCast;
        this.qReceive = qReceive;
    }
    
    protected void setLocalMemory(LocalMemory localMemory)
    {
        this.localMemory = localMemory;
    }
    /**
     * Inserts the specified element at the tail of the broadcast queue, 
     * waiting for space to become available if the queue is full. 
     * This method is synchronized to prevent the DSM from executing 
     * multiple broadcasts while this thread is already waiting on the queue.
     * @param message   The element to insert into the queue.
     */
    protected synchronized void broadcast(Message<String, Object> message)
    {
        try 
        {
            qBroadcast.put(message); //blocks if the queue is full
        } 
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves the specified element at the head of the receive queue
     * or null if the queue is empty. This queue is populated by the
     * BroadcastSytem from its own queue which in turn is filled
     * from broadcasts.
     * @returns message   The element at the head of the queue.
     */
    protected Message<String, Object> receive()
    {
        return qReceive.poll();
    }
    
    /**
     * BroadcastAgent will actively process its message queue.
     * This means that while running, this thread will poll the queue
     * and perform stores on the local memory cache.
     */
    @Override
    public void run()
    {
        while(true)
        {
            
            Message<String, Object> message = qReceive.poll();
            if(message != null)
            {
                localMemory.store(message.getKey(), message.getValue());
            }
        }
        
    }
}
