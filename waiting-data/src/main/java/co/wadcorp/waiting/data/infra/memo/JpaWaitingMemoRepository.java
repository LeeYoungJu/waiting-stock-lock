package co.wadcorp.waiting.data.infra.memo;

import co.wadcorp.waiting.data.domain.memo.WaitingMemoEntity;
import co.wadcorp.waiting.data.domain.memo.WaitingMemoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaWaitingMemoRepository extends JpaRepository<WaitingMemoEntity, Long>,
    WaitingMemoRepository {

}
