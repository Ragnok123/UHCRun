package ultraHardcore.arena.task;

import cn.nukkit.scheduler.AsyncTask;
import ultraHardcore.arena.Arena;

public class StartArenaTask extends AsyncTask {
      private final String map;

      public StartArenaTask(Arena arena) {
            this.map = arena.getId();
      }

      public void onRun() {
      }
}
