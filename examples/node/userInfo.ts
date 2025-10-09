import { post } from "./common.js";

post("/v2/acquiring/user/info", {}).then(console.log).catch(e => console.error(e.response?.data || e));