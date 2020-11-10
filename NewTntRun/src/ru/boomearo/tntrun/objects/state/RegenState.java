package ru.boomearo.tntrun.objects.state;

import ru.boomearo.tntrun.objects.Arena;

public class RegenState implements IGameState {

    @Override
    public void initState(Arena arena) {
        //TODO надеюсь не будет рекурскии. Но пока что будет сделано так.
        arena.setGameState(new WaitingState());
    }
    
    
    @Override
    public void autoUpdateHandler(Arena arena) {}

    @Override
    public void endState(Arena arena) {}

}
