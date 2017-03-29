
public class Token {
    protected String id = "all";
    protected Token()
    {
        
    }
    
    protected String getID()
    {
        return this.id;
    }
    
    protected synchronized void setID(String id)
    {
        this.id = id;
    }
}
