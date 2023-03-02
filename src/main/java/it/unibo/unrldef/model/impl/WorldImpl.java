package it.unibo.unrldef.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.*;

public class WorldImpl implements World{

    final String name;
    final Integrity castleIntegrity;
    //final Bank bank;
    final Path path;
    final List<Wave> waves;
    int waveCounter;
    final Map<Position, Optional<Tower>> placedTowers = new HashMap<>();
    final Set<Tower> availableTowers;
    final List<Enemy> livingEnemies = new ArrayList<>();



    private WorldImpl(String name, Integrity castleIntegrity, Path path, List<Wave> waves, Set<Tower> availableTowers){
        this.name = name;
        this.castleIntegrity = castleIntegrity;
        this.path = path;
        this.waves = waves;
        this.availableTowers = availableTowers;
    }

    public void updateState(long time){
        this.livingEnemies.stream().forEach(x -> x.updateState(time));
        this.placedTowers.values().stream().filter(Optional::isPresent).forEach(x -> x.get().updateState(time));
        if (this.waves.get(waveCounter).isFinished()) {
            this.waveCounter++;
        }
    }

    public void startGame(){
        this.waves.get(0).getNextHorde();
    }

    @Override
    private void startWave(Wave wave) {
        // TODO Auto-generated method stub
        
    }



    @Override
    public void buildTower(Position pos, Tower tower) {
        this.placedTowers.add(pos, tower);
        
    }

    @Override
    public List<Entity> getSceneEntities() {
        List<Entity> ret = new ArrayList<Entity>();
        ret.addAll(this.availableTowers);
        ret.addAll(this.livingEnemies);
        return ret;
    }

    @Override
    public Integrity getCastleIntegrity() {
        return this.CastleIntegrity;
    }

     
    @Override
    public int getMoney() {
        // TODO Auto-generated method stub
        return 0;
    } 

    @Override
    public Set<Tower> getAvailableTowers() {
        return this.availableTowers;
    }

    @Override
    public Path getPath() {
        return this.path;
    }

	@Override
	public List<Enemy> sorroundingEnemies(Position center, double radius) {
		return this.livingEnemies.stream().filter(x -> (distance(center, x.getPosition().get()) <= radius )).toList();
	}  
    
    private double distance( Position a, Position b ) {
        return Math.sqrt(Math.pow((a.getX() - b.getX()), 2) + Math.pow((a.getY() - b.getY()), 2));
    }
}
