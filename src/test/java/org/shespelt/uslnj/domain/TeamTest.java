package org.shespelt.uslnj.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class TeamTest {

    Team ourTeam;
    Field theField;
    Division ourDivision;

    static String ourField = "the field";

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        ourDivision = new Division();
        ourDivision.setName("TheDivision");
        ourDivision.setId(3L );
        ourTeam = new Team( ourDivision, "my team");
        theField = new Field();
        theField.setName( ourField );
        //ourTeam.setFieldId(ourField);
        ourTeam.setHomeField( theField );
        List<Integer> badDays = new ArrayList<Integer>();
        badDays.add( 2 );
        badDays.add( 4 );
        ourTeam.setUnableToPlay( badDays );
        //ourTeam.setOurDivision( ourDivision );
    }

    @org.junit.jupiter.api.Test
    void inOurDivision() {
    }

    @org.junit.jupiter.api.Test
    void isUnavailable() {
    }

    @Test
    void addHomeField() {
        ourTeam.setHomeField( theField );
        assertEquals( theField, ourTeam.getHomeField());
    }

    @org.junit.jupiter.api.Test
    void testToString() {
        System.out.println( "The team toString: " + ourTeam.toString() );
    }
}