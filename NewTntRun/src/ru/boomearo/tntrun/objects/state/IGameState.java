package ru.boomearo.tntrun.objects.state;

import ru.boomearo.tntrun.objects.Arena;

public interface IGameState {

    public void initState(Arena arena);
    
    public void autoUpdateHandler(Arena arena);
}
