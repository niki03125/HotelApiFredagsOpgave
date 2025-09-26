package app.daos;

import app.dtos.PoemDTO;
import app.entities.Poem;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class PoemDAO implements IDAO<PoemDTO, Integer> {
    private final EntityManagerFactory emf;

    public PoemDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void createAll(List<PoemDTO> poemDTOs) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin(); // Start én transaktion for hele batchen

        for (PoemDTO dto : poemDTOs) {
            Poem poem = Poem.builder()
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .author(dto.getAuthor())
                    .type(dto.getType())
                    .build();

            em.persist(poem); // Gem entity, ikke DTO
        }

        em.getTransaction().commit(); // Commit én gang for alle
        em.close();
    }


    @Override
    public PoemDTO create(PoemDTO poem) {
        try (EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.persist(poem);
            em.getTransaction().commit();
        }
        return poem;
    }

    @Override
    public List<PoemDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()){
            TypedQuery<PoemDTO> query = em.createQuery("SELECT p FROM Poem p", PoemDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public PoemDTO getById(Integer id) {
        try(EntityManager em = emf.createEntityManager()){
            return em.find(PoemDTO.class, id);
        }
    }

    @Override
    public PoemDTO update(PoemDTO poem) {
        try (EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            PoemDTO updatedPoem = em.merge(poem);
            em.getTransaction().commit();
            return updatedPoem;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try(EntityManager em = emf.createEntityManager()){
            PoemDTO poemToDelete = em.find(PoemDTO.class, id);
            if(poemToDelete != null){
                em.getTransaction().begin();
                em.remove(poemToDelete);
                em.getTransaction().commit();
                return true;
            }else{
                return false;
            }
        }catch(ApiException ex){
            return false;
        }
    }
}