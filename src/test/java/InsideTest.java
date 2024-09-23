import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InsideTest {

    @Test
    void insideRoom() {
        Logic logic = new Logic();
        assertTrue(logic.insideRoom(1,1,0,0));
    }
}