<#import "./base.ftl" as base>

<@base.base title="Set Up" context="setup">

    <div class="container container--white mt-3">

        <div class="row">
            <div class="col-sm-12">
                <h2>Set Up Fleet</h2>

                <p>
                    It looks like this is the first time you're running Fleet. In order to get started, you need to create
                    and initial user which will have access to the management pages of the application.
                </p>

                <h3 class="mb-3">Create a user</h3>

                <form action="/setup" method="POST" class="needs-validation" novalidate>
                    <div class="form-group row">
                        <label for="username" class="col-sm-2 col-form-label">Username</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control form-control-sm" name="username" id="username" required />
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="password" class="col-sm-2 col-form-label">Password</label>
                        <div class="col-sm-10">
                            <input type="password" class="form-control form-control-sm" name="password" id="password" required />
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="verify-password" class="col-sm-2 col-form-label">Verify Password</label>
                        <div class="col-sm-10">
                            <input type="password" class="form-control form-control-sm" name="verify-password" id="verify-password" required />
                        </div>
                    </div>
                    <div class="form-group text-center">
                        <button type="submit" class="btn btn-primary">Continue</button>
                    </div>
                </form>
            </div>
        </div>

    </div>

</@base.base>