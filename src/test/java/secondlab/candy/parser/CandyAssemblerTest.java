package secondlab.candy.parser;

import secondlab.candy.error.CandyXmlException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CandyAssemblerTest {
    @Test
    void rejectsAnUnknownFieldAndBlankValues() {
        CandyAssembler assembler = new CandyAssembler();
        assertThrows(CandyXmlException.class, () -> assembler.setField("extra", "1"));
        assembler.setFilling("   ");
        assembler.setField("name", null);
        assembler.setField("kind", "");
        assertThrows(CandyXmlException.class, assembler::build);
    }
}
