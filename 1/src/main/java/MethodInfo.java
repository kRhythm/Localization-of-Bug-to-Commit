import java.util.List;

public class MethodInfo {
    private List<Integer> start;   
    public List<Integer> start() {
        return start;
    }
    public void addstart(Integer start) {
        this.start.add(start);
    }
  }