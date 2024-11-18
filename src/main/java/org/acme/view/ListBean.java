import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.util.ArrayList;
import java.util.List;

@Named  // ("index2")  // It doesn't work with "index2"
@ViewScoped
public class ListBean {
    private List<ListItem> items;

    @PostConstruct
    public void init() {
        items = new ArrayList<>();
        items.add(new ListItem("Value1A", "Value1B"));
        items.add(new ListItem("Value2A", "Value2B"));
        // Add more items as needed
    }

    public List<ListItem> getItems() {
        return items;
    }
}
