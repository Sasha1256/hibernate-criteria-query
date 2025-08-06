package ma.hibernate.dao;

import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
            return phone;
        } catch (Exception exception) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Could not create phone", exception);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = criteriaQuery.from(Phone.class);
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String key = entry.getKey();
                String[] value = entry.getValue();
                Phone.class.getDeclaredField(key);
                CriteriaBuilder.In<Object> in = criteriaBuilder.in(phoneRoot.get(key));
                for (String s : value) {
                    in.value(s);
                }
                criteriaQuery.where(criteriaBuilder.and(in));
            }
            return session.createQuery(criteriaQuery).getResultList();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Could not find Phone by ID", e);
        }
    }
}
