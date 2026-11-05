import { classes } from '@automapper/classes';
import { createMapper, Mapper } from '@automapper/core';

export const mapper: Mapper = createMapper({
  strategyInitializer: classes()
});
