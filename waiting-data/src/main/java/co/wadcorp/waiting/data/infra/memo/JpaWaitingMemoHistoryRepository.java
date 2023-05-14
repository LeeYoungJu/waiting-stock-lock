package co.wadcorp.waiting.data.infra.memo;

import co.wadcorp.waiting.data.domain.memo.WaitingMemoHistoryEntity;
import co.wadcorp.waiting.data.domain.memo.WaitingMemoHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaWaitingMemoHistoryRepository extends JpaRepository<WaitingMemoHistoryEntity, Long>,
    WaitingMemoHistoryRepository {

}
