

public class Lock {
    private String id = "done";
    protected Lock()
    {
        
    }
    
    protected synchronized void setID(String id)
    {
        this.id = id;
    }
    
    protected String getID()
    {
        return this.id;
    }
    
    protected synchronized void done()
    {
        setID("done");
    }
}
