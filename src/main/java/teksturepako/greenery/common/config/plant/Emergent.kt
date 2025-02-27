package teksturepako.greenery.common.config.plant

import net.minecraftforge.common.config.Config
import teksturepako.greenery.common.config.plant.emergent.Arrowhead
import teksturepako.greenery.common.config.plant.emergent.Cattail

class Emergent
{
    @Config.Name("Arrowhead")
    @Config.Comment("Options for Arrowhead")
    @JvmField
    val arrowhead = Arrowhead()

    @Config.Name("Cattail")
    @Config.Comment("Options for Cattail")
    @JvmField
    val cattail = Cattail()
}