package com.valkryst.test.object;

import com.valkryst.dds.object.ComparisonType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ComparisonTypeTest {
    @Test
    public void getComparisonTypeByName() {
        assertEquals(ComparisonType.EQUAL_TO, ComparisonType.getComparisonTypeByName("="));
        assertEquals(ComparisonType.EQUAL_TO, ComparisonType.getComparisonTypeByName("EQUAL_TO"));

        assertEquals(ComparisonType.LESS_THAN, ComparisonType.getComparisonTypeByName("<"));
        assertEquals(ComparisonType.LESS_THAN, ComparisonType.getComparisonTypeByName("LESS_THAN"));

        assertEquals(ComparisonType.GREATER_THAN, ComparisonType.getComparisonTypeByName(">"));
        assertEquals(ComparisonType.GREATER_THAN, ComparisonType.getComparisonTypeByName("GREATER_THAN"));

        assertEquals(ComparisonType.LESS_THAN_OR_EQUAL_TO, ComparisonType.getComparisonTypeByName("<="));
        assertEquals(ComparisonType.LESS_THAN_OR_EQUAL_TO, ComparisonType.getComparisonTypeByName("LESS_THAN_OR_EQUAL_TO"));

        assertEquals(ComparisonType.GREATER_THAN_OR_EQUAL_TO, ComparisonType.getComparisonTypeByName(">="));
        assertEquals(ComparisonType.GREATER_THAN_OR_EQUAL_TO, ComparisonType.getComparisonTypeByName("GREATER_THAN_OR_EQUAL_TO"));

        assertEquals(ComparisonType.NOT_EQUAL_TO, ComparisonType.getComparisonTypeByName("!="));
        assertEquals(ComparisonType.NOT_EQUAL_TO, ComparisonType.getComparisonTypeByName("NOT_EQUAL_TO"));
    }
}
