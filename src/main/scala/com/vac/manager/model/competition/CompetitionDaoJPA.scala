package com.vac.manager.model.competition

import com.vac.manager.model.generic.GenericDaoJPA
import org.springframework.stereotype.Repository

@Repository("competitionDao")
class CompetitionDaoJPA
  extends GenericDaoJPA[Competition, java.lang.Long](classOf[Competition])
  with CompetitionDao {

}
