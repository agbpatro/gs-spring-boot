package service.db.impl;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import service.db.dao.PersonDao;
import service.db.model.Ad;
import service.db.model.Person;

import static service.Application.getConnection;
import static service.response.ResultWrapper.getAd;
import static service.response.ResultWrapper.getPerson;

/**
 * Created by abhishek on 10/2/16.
 */
public class PersonDaoImpl implements PersonDao {

  final static Logger LOG = Logger.getLogger(PersonDao.class);
  final static ConcurrentHashMap<Integer, Person> userCache1 = new ConcurrentHashMap<>();
  final static ConcurrentHashMap<String, Person> userCache2 = new ConcurrentHashMap<>();


  @Override
  public Person insertPerson(Person model) {
    Person p = getPersonByName(model);
    if (p != null) {
      return p;
    }
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
          LOG.error("Error closing connection", e);
        }
      }
    }
    return null;

  }

  @Override
  public Person getPersonById(Person model) {

    if (userCache1.containsKey(model.getId())) {
      LOG.info("Cache hit for person id");
      return userCache1.get(model.getId());
    }
    String sql = "Select * from Person where id = ?";
    Connection conn = getConnection();
    try {
      //conn = dataSource.getConnection();
      //conn = getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, model.getId());
      //int count = pstmt.executeUpdate();
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        Person temp = getPerson(rs);
        userCache1.put(temp.getId(), temp);
        userCache2.put(temp.getName(), temp);
        return temp;
      }
    } catch (SQLException e) {
      LOG.error("Error getting person by id", e);
    } finally {
      if (conn == null) {
        try {
          conn.close();
        } catch (SQLException e) {
          LOG.error("Error closing connection", e);
        }
      }
    }
    return null;
  }

  @Override
  public Person getPersonByName(Person model) {

    if (userCache2.containsKey(model.getName())) {
      LOG.info("Cache hit for person name");
      return userCache2.get(model.getName());
    }
    String sql = "Select * from Person where name = ?";
    Connection conn = getConnection();
    try {
      //conn = dataSource.getConnection();
      //conn = getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, model.getName());
      //int count = pstmt.executeUpdate();
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        Person temp = getPerson(rs);
        userCache1.put(temp.getId(), temp);
        userCache2.put(temp.getName(), temp);
        return temp;
      }
    } catch (SQLException e) {
      LOG.error("Error getting person by id", e);
    } finally {
      if (conn == null) {
        try {
          conn.close();
        } catch (SQLException e) {
          LOG.error("Error closing connection", e);
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
        Person temp = getPerson(rs);
        userCache1.put(temp.getId(), temp);
        userCache2.put(temp.getName(), temp);
        personList.add(getPerson(rs));
      }
    } catch (SQLException e) {
      LOG.error("Error getting all persons", e);
    } finally {
      if (conn == null) {
        try {
          conn.close();
        } catch (SQLException e) {
          LOG.error("Error closing connection", e);
        }
      }
    }
    return personList;
  }

  @Override
  public List<Ad> getAllPersonAds(Person model) {
    Person person = getPersonByName(model);
    String
        sql =
        "Select * from AD where id IN (Select adId from PERSONAD where personId = (Select id from  Person where name = ?))";
    Connection conn = getConnection();
    List<Ad> personAdList = new ArrayList<>();

    try {
      //conn = dataSource.getConnection();
      //conn = getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, person.getName());
      //int count = pstmt.executeUpdate();
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        Ad temp = getAd(rs);
        temp.setPersonId(person.getId());
        personAdList.add(temp);
      }
    } catch (SQLException e) {
      LOG.error("Error getting all person ads", e);
    } finally {
      if (conn == null) {
        try {
          conn.close();
        } catch (SQLException e) {
          LOG.error("Error closing connection", e);
        }
      }
    }

    return personAdList;
  }
}
