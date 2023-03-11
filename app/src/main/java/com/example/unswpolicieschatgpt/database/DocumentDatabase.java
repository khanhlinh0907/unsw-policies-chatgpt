package com.example.unswpolicieschatgpt.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import java.util.ArrayList;

//Add database entities
@Database(entities = {Document.class}, version = 1, exportSchema = false)
public abstract class DocumentDatabase extends RoomDatabase {
    //Define database name
    private static final String DATABASE_NAME = "Document_Database";
    //Create database instance
    private static DocumentDatabase database;

    //Create Dao
    public abstract DocumentDao mainDao();

    public ArrayList<Document> insertDocument() {
        DocumentDao mainDao = database.mainDao();
        ArrayList<Document> documentList = new ArrayList<>();
        mainDao.insert(new Document(1, "Assessment Design Procedure", "To specify the processes and " +
                "responsibilities for the design of assessment of student learning.", "The procedure applies to: " +
                "- assessment in all undergraduate, honours and postgraduate coursework programs, " +
                "the coursework component of higher degree research programs and non-award courses " +
                "offered by or on behalf of UNSW; and " +
                "- all students, staff and others associated with, or contracted by, UNSW who are " +
                "responsible for assessment in these programs.", true, "1. Assessment is designed to " +
                "guide and enhance student learning (Policy Principle 1) " +
                "1.1. Aligning assessment with learning outcomes " +
                "Assessment requirements for all UNSW’s programs and courses will be designed to assess the " +
                "attainment of program and/or course level learning outcomes consistent with the " +
                "Integrated Curriculum Framework. The assessment requirements within programs and " +
                "courses will include a variety of tasks determined by the range of learning outcomes. " +
                "No single assessment task, including examinations but excluding research- or project-based " +
                "assessments and theses, will be weighted more than 60% of the overall course result. " +
                "Assessment requirements of accreditation bodies are exempt from this limit. Courses " +
                "with project-based assessment tasks should stipulate the weighting of marks/grades " +
                "related to each learning outcome assessed by the project. Where assessment entails " +
                "students working in groups to prepare and/or present a single product or performance, " +
                "and for which the contributions of individual students are not assessed separately, " +
                "the assessment will constitute no more than 30% of the overall course result. Course " +
                "outlines will include a statement of the assessment tasks noting their alignment to " +
                "the course and program learning outcomes (if applicable) and the weighting of assessment " +
                "tasks to the overall course result.", "https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/assessmentpolicy.pdf" +
                "Assessment Implementation Procedure: https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/assessmentimplementationprocedure.pdf",
                "Pro-Vice-Chancellor (Education and Student Experience)","Deputy Vice-Chancellor Academic and Student Life",
                "https://www.unsw.edu.au/content/dam/pdfs/governance/policy/2022-01-policies/assessmentdesignprocedure.pdf"));
        return documentList;
    }
}
