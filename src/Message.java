import java.util.Map.Entry;

public class Message<K, V> implements Entry<K, V> {
    private K key;
    private V value;
    protected OPCODE opcode;
    protected Object sender;
    
    public Message(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Message(Message message)
    {
        this.key = (K) message.getKey();
        this.value = (V) message.getValue();
        this.opcode = message.opcode;
        this.sender = message.sender;
    }
    
    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }
    
    protected void configure(OPCODE opcode, Object sender)
    {
        this.opcode = opcode;
        this.sender = sender;
    }
    
}

