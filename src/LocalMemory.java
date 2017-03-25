import java.util.HashMap;
import java.util.Map;

public class LocalMemory {

    private Map<String, Object> memory;
    
    protected LocalMemory()
    {
        memory = new HashMap<String, Object>();
    }
    
    /**
     * @param key       New keys can be added but the value of existing keys will be overwritten 
     * @param value     This parameter will be auto-boxed as an Object 
     */
    protected void store(String key, Object value)
    {
        memory.put(key, value);
    }
    
    /**
     * @param key       This parameter is used to retrieve the associated value
     * @return          The associated value is unboxed to the specified type by cast or is null.
     */
    @SuppressWarnings("unchecked")
    protected <T> T load(String key)
    {
        return (T) memory.get(key);
    }
    
    /**
     * @return          Generate a new 'key : ClassName\n' string for every pair and concatenate them all together or returns 'Memory is empty' if no keys have been stored
     */
    @Override
    public String toString()
    {
        String s = "";
        
        //Generate key : class name string
        if(memory.isEmpty())
        {
            s =  "Memory is empty";
        }
        else
        {
            for ( String key : memory.keySet())
            {
                s += String.format("{0} : {1}\n", key, memory.get(key).getClass().getName());
            }
        }
        
        return s;
    }
    
}
