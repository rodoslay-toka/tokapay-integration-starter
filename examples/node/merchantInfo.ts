import { post } from "./common.js";

post("/v2/acquiring/merchant/info", {}).then(console.log).catch(e => console.error(e.response?.data || e));