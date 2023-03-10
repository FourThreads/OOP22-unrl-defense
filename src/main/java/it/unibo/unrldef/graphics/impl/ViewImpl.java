package it.unibo.unrldef.graphics.impl;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import it.unibo.unrldef.graphics.api.View;
import it.unibo.unrldef.input.api.Input;
import it.unibo.unrldef.model.api.Player;
import it.unibo.unrldef.model.api.Spell;
import it.unibo.unrldef.model.api.World;
import it.unibo.unrldef.model.impl.FireBall;
import it.unibo.unrldef.model.impl.Arrows;
import it.unibo.unrldef.model.impl.SpellImpl;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewImpl implements View{

    private final GamePanel gamePanel;
    private final JFrame frame;
    private final Player player;
    private final JButton fireBall;
    private final JButton arrows;

    public ViewImpl(Player player, World world, Input inputHandler){
        this.player = player;
        this.frame = new JFrame("Unreal Defense");
		this.frame.setSize(1280,720);
		//this.frame.setMinimumSize(new Dimension(1280,720));
		this.frame.setResizable(true);
		// frame.setUndecorated(true); // Remove title bar
		//TODO: resize handler with scale on GamePanel
		this.gamePanel = new GamePanel(world, inputHandler);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        JButton cannon = new JButton("CANNON");
        cannon.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.setState(GamePanel.ViewState.TOWER_SELECTED);
				gamePanel.setSelectedEntity("cannon");
            }
            
        });

        JButton hunter = new JButton("HUNTER");
        hunter.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
				gamePanel.setState(GamePanel.ViewState.TOWER_SELECTED);
				gamePanel.setSelectedEntity("hunter");
                
            }
            
        });

        this.fireBall = new JButton("FIREBALL");
        fireBall.setEnabled(false);
        fireBall.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
				gamePanel.setState(GamePanel.ViewState.SPELL_SELECTED);
				gamePanel.setSelectedEntity("fireball");
                
            }
            
        });

        this.arrows = new JButton("ARROWS");
        arrows.setEnabled(false);
        arrows.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
				gamePanel.setState(GamePanel.ViewState.SPELL_SELECTED);
				gamePanel.setSelectedEntity("arrows");
            }
            
        });
		
		buttonPanel.add(cannon);
		buttonPanel.add(hunter);
		buttonPanel.add(fireBall);
		buttonPanel.add(arrows);
		this.frame.getContentPane().add(this.gamePanel, BorderLayout.CENTER);
		this.frame.getContentPane().add(buttonPanel, BorderLayout.EAST);
		this.frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent ev){
				System.exit(-1);
			}
			public void windowClosed(WindowEvent ev){
				System.exit(-1);
			}
		});
		this.frame.pack();
		this.frame.setVisible(true);
    }

    @Override
    public void render() {
        this.updateButtons();
        this.gamePanel.repaint();
    }

    private void updateButtons() {
        for (Spell spell : player.getSpells()) {
            switch (spell.getName()) {
                case FireBall.NAME:
                    this.fireBall.setEnabled(((SpellImpl)spell).isReady());
                    break;
                case Arrows.NAME:
                    this.arrows.setEnabled(((SpellImpl)spell).isReady());
                    break;
                default:
                    break;
            }
        }
    }
}
