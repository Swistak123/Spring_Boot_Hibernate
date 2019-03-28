package pl.edu.agh.ki.mwo.persistence;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import pl.edu.agh.ki.mwo.model.School;
import pl.edu.agh.ki.mwo.model.SchoolClass;
import pl.edu.agh.ki.mwo.model.Student;

public class DatabaseConnector {
	
	protected static DatabaseConnector instance = null;
	
	public static DatabaseConnector getInstance() {
		if (instance == null) {
			instance = new DatabaseConnector();
		}
		return instance;
	}
	
	Session session;

	protected DatabaseConnector() {
		session = HibernateUtil.getSessionFactory().openSession();
	}
	
	public void teardown() {
		session.close();
		HibernateUtil.shutdown();
		instance = null;
	}
	
	public Iterable<School> getSchools() {
		String hql = "FROM School";
		Query query = session.createQuery(hql);
		List schools = query.list();
		
		return schools;
	}

	public void addSchool(School school) {
		Transaction transaction = session.beginTransaction();
		session.save(school);
		transaction.commit();
	}
	
	public void deleteSchool(String schoolId) {
		String hql = "FROM School S WHERE S.id=" + schoolId;
		Query query = session.createQuery(hql);
		List<School> results = query.list();
		Transaction transaction = session.beginTransaction();
		for (School s : results) {
			session.delete(s);
		}
		transaction.commit();
	}

	public Iterable<SchoolClass> getSchoolClasses() {
		String hql = "FROM SchoolClass";
		Query query = session.createQuery(hql);
		List schoolClasses = query.list();
		
		return schoolClasses;
	}
	
	public void addSchoolClass(SchoolClass schoolClass, String schoolId) {
		String hql = "FROM School S WHERE S.id=" + schoolId;
		Query query = session.createQuery(hql);
		List<School> results = query.list();

		Transaction transaction = session.beginTransaction();
		if (results.size() == 0) {
			session.save(schoolClass);
		} else {
			School school = results.get(0);
			school.addClass(schoolClass);
			schoolClass.setSchool(school);
			session.save(school);
		}
		transaction.commit();
	}
	
	public void deleteSchoolClass(String schoolClassId) {
		String hql = "FROM SchoolClass S WHERE S.id=" + schoolClassId;
		Query query = session.createQuery(hql);
		List<SchoolClass> results = query.list();
		Transaction transaction = session.beginTransaction();
		for (SchoolClass s : results) {
			s.getSchool().removeSchoolClass(s);
			session.delete(s);
		}
		transaction.commit();
	}

//    SELECT students.*, schoolClasses.profile FROM students
//    INNER JOIN schoolClasses on students.class_id=schoolClasses.id


    public Iterable<Student> getStudents() {
        String hql = "FROM Student";
        Query query = session.createQuery(hql);
        List students = query.list();

        return students;
	}

    public void deleteStudent(String studentId) {
	    String hql = "FROM Student S WHERE S.id=" + studentId;
	    Query query = session.createQuery(hql);
	    List<Student> results = query.list();

	    Transaction transaction = session.beginTransaction();
        for (Student s : results) {
			s.getSchoolClass().removeStudent(s);
            session.delete(s);
        }
        transaction.commit();
    }

    public void addStudent(Student student, String schoolClassId) {
	    String hql = "FROM SchoolClass SC WHERE SC.id=" + schoolClassId;
	    Query query = session.createQuery(hql);
	    List<SchoolClass> results = query.list();

	    Transaction transaction = session.beginTransaction();
	    if (results.size() == 0) {
	        session.save(student);
        } else {
	        SchoolClass schoolClass = results.get(0);
	        schoolClass.addStudent(student);
	        student.setSchoolClass(schoolClass);
	        session.save(student);
        }
        transaction.commit();
    }

    public void editSchool(Long schoolId, String newName, String newAddress) {
		Transaction transaction = session.beginTransaction();
		School school = (School) session.get(School.class, schoolId);

		school.setName(newName);
		school.setAddress(newAddress);
		transaction.commit();
	}

	public void editSchoolClass(Long schoolClassId, int newStartYear, int newCurrentYear,
                                String newProfile, String schoolId) {
        String hql = "FROM School S WHERE S.id=" + schoolId;
        Query query = session.createQuery(hql);
        List<School> schools = query.list();

		Transaction transaction = session.beginTransaction();

		SchoolClass schoolClass = (SchoolClass) session.get(SchoolClass.class, schoolClassId);

		schoolClass.setStartYear(newStartYear);
		schoolClass.setCurrentYear(newCurrentYear);
		schoolClass.setProfile(newProfile);

		if (schools.size() == 0) {
			session.save(schoolClass);
		} else {
			School school = schools.get(0);
			school.addClass(schoolClass);
			session.save(school);
		}
		transaction.commit();
	}

    public void editStudent(long studentId, String newName, String newSurname,
                            String newPesel, String schoolClassId) {
	    String hql = "FROM SchoolClass SC WHERE SC.id=" + schoolClassId;
	    Query query = session.createQuery(hql);
	    List<SchoolClass> schoolClasses = query.list();

	    Transaction transaction = session.beginTransaction();

	    Student student = (Student) session.get(Student.class, studentId);

	    student.setName(newName);
	    student.setSurname(newSurname);
	    student.setPesel(newPesel);

        if (schoolClasses.size() == 0) {
            session.save(student);
        } else {
            SchoolClass schoolClass = schoolClasses.get(0);
            schoolClass.addStudent(student);
            session.save(student);
        }
        transaction.commit();
    }

    public Student getStudentById(String studentId) {
		return (Student) session.get(Student.class, Long.parseLong(studentId));
    }

    public School getSchoolById(String schoolId) {
		return (School) session.get(School.class, Long.parseLong(schoolId));
    }

    public SchoolClass getSchoolClassById(String schoolClassId) {
		return (SchoolClass) session.get(SchoolClass.class, Long.parseLong(schoolClassId));
    }
}
