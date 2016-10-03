package service.db.impl;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import service.Application;
import service.db.dao.PersonDao;
import service.db.model.Person;

import static service.Application.getConnection;
import static service.response.ResultWrapper.getPerson;

/**
 * Created by abhishek on 10/2/16.
 */
public class PersonDaoImpl implements PersonDao {

  final static Logger LOG = Logger.getLogger(PersonDao.class);

  @Override
  public Person insertPerson(Person model) {
    String sql =
        "INSERT INTO PERSON (NAME, CITY, AGE) VALUES (?,?,?) Returning *";
    Connection conn = getConnection();

    try {
      //conn = dataSource.getConnection();
      //conn = getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, model.getName());
      pstmt.setString(2, model.getCity());
      pstmt.setInt(3, model.getAge());
      //int count = pstmt.executeUpdate();
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return getPerson(rs);
      }
    } catch (SQLException e) {
      LOG.error("Error inserting person", e);
    } finally {
      if (conn == null) {
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return null;

  }

  @Override
  public List<Person> getAllPersons() {
    String sql = "Select * from Person";
    Connection conn = getConnection();
    List<Person> personList = new ArrayList<>();

    try {
      //conn = dataSource.getConnection();
      //conn = getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      //int count = pstmt.executeUpdate();
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        personList.add(getPerson(rs));
      }
    } catch (SQLException e) {
      LOG.error("Error getting all persons", e);
    } finally {
      if (conn == null) {
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return personList;
  }
}
