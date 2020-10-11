package com.javazilla.bukkitfabric.impl.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

import com.javazilla.bukkitfabric.interfaces.IMixinEntity;

public abstract class ProjectileImpl extends AbstractProjectile implements Projectile {

    public ProjectileImpl(CraftServer server, net.minecraft.entity.projectile.ProjectileEntity entity) {
        super(server, entity);
    }

    @Override
    public ProjectileSource getShooter() {
        return ((IMixinEntity)getHandle()).getProjectileSourceBukkit();
    }

    @Override
    public void setShooter(ProjectileSource shooter) {
        if (shooter instanceof CraftLivingEntity) getHandle().setOwner((LivingEntity) ((CraftLivingEntity) shooter).nms);
        else getHandle().setOwner(null);
        ((IMixinEntity)getHandle()).setProjectileSourceBukkit(shooter);
    }

    @Override
    public ProjectileEntity getHandle() {
        return (ProjectileEntity) nms;
    }

    @Override
    public String toString() {
        return "CraftProjectile";
    }

}