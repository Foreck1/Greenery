@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")

package teksturepako.greenery.common.block.plant.submerged

import net.minecraft.block.IGrowable
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import teksturepako.greenery.Greenery
import teksturepako.greenery.common.config.Config
import java.util.*

class BlockSeagrass : AbstractSubmergedPlant(NAME), IGrowable
{
    enum class SeagrassVariant : IStringSerializable
    {
        SINGLE, BOTTOM, TOP;

        override fun getName(): String
        {
            return name.toLowerCase()
        }

        override fun toString(): String
        {
            return getName()
        }
    }

    companion object
    {
        const val NAME = "seagrass"
        const val REGISTRY_NAME = "${Greenery.MODID}:$NAME"

        val VARIANT: PropertyEnum<SeagrassVariant> = PropertyEnum.create("variant", SeagrassVariant::class.java)
    }

    init
    {
        defaultState = blockState.baseState.withProperty(VARIANT, SeagrassVariant.SINGLE)
    }

    override val compatibleFluids: MutableList<String>
        get() = Config.plant.submerged.seagrass.compatibleFluids.toMutableList()

    override fun getStateFromMeta(meta: Int): IBlockState
    {
        return defaultState
    }

    override fun getMetaFromState(state: IBlockState): Int
    {
        return 0
    }

    override fun createBlockState(): BlockStateContainer
    {
        return BlockStateContainer(this, VARIANT)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState
    {
        val hasSeagrassBelow = worldIn.getBlockState(pos.down()).block == this
        val hasSeagrassAbove = worldIn.getBlockState(pos.up()).block == this

        return state.withProperty(
            VARIANT, when
            {
                hasSeagrassBelow -> SeagrassVariant.TOP
                hasSeagrassAbove -> SeagrassVariant.BOTTOM
                else             -> SeagrassVariant.SINGLE
            }
        )
    }

    @SideOnly(Side.CLIENT)
    override fun getOffsetType(): EnumOffsetType
    {
        return EnumOffsetType.XZ
    }

    override fun canBlockStay(worldIn: World, pos: BlockPos): Boolean
    {
        //Must have a SINGLE weed or valid soil below
        val down = worldIn.getBlockState(pos.down())
        val down2 = worldIn.getBlockState(pos.down(2))
        return if (down.block == this)
        {         // if block down is weed
            down2.block != this                  // if 2 block down is weed return false
        }
        else down.material in ALLOWED_SOILS    // if block down is not weed return if down is in ALLOWED_SOILS
    }

    // IGrowable implementation
    override fun canGrow(worldIn: World, pos: BlockPos, state: IBlockState, isClient: Boolean): Boolean
    {
        val actualState = state.getActualState(worldIn, pos)
        return actualState.getValue(VARIANT) == SeagrassVariant.SINGLE && canGenerateBlockAt(worldIn, pos.up())
    }

    override fun grow(worldIn: World, rand: Random, pos: BlockPos, state: IBlockState)
    {
        worldIn.setBlockState(pos.up(), state)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB
    {
        return when (val actualState = getActualState(state, source, pos))
        {
            actualState.withProperty(VARIANT, SeagrassVariant.TOP)    -> TOP_AABB.offset(state.getOffset(source, pos))
            actualState.withProperty(VARIANT, SeagrassVariant.SINGLE) -> TOP_AABB.offset(state.getOffset(source, pos))
            actualState.withProperty(VARIANT, SeagrassVariant.BOTTOM) -> BOTTOM_AABB.offset(
                state.getOffset(
                    source, pos
                )
            )
            else                                                      -> TOP_AABB.offset(state.getOffset(source, pos))
        }
    }
}