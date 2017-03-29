

public class Lock {
    private String id = "done";
    private Object returnValue = null;
    
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
    
    protected synchronized void setReturn(Object value)
    {
        this.returnValue = value;
    }
    
    protected synchronized Object getValue()
    {
        return this.returnValue;
    }
}
